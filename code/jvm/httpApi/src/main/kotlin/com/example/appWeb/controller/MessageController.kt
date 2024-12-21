package com.example.appWeb.controller

import com.example.appWeb.model.dto.input.message.CreateMessageInputModel
import com.example.appWeb.model.dto.input.user.AuthenticatedUserInputModel
import com.example.appWeb.model.dto.output.message.MessageOutputModel
import com.example.appWeb.model.problem.ChannelProblem
import com.example.appWeb.model.problem.MessageProblem
import com.example.appWeb.model.problem.UserProblem
import com.example.appWeb.swagger.MessageSwaggerConfig
import errors.MessageError.ChannelNotFound
import errors.MessageError.InvalidMessageInfo
import errors.MessageError.UserNotFound
import interfaces.MessageServicesInterface
import interfaces.SseServiceInterface
import io.swagger.v3.oas.annotations.Parameter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import model.messages.Message
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import utils.Failure
import utils.Success
import java.io.IOException
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.sql.Timestamp

/**
 * The default limit for the message list.
 */
private const val OFFSET = 0u

/**
 * The default offset for the message list.
 */
private const val LIMIT = 100u

/**
 * The default timeout for the message list.
 */
private const val DEFAULT_TIMEOUT = 1000000L

/**
 * The keep alive timeout for the message list.
 */
private const val KEEP_ALIVE_TIMEOUT = 5000L

/**
 * Represents the controller for the message
 *
 * @property messageService The message service
 */
@RestController
@RequestMapping(MessageController.MESSAGE_BASE_URL)
class MessageController(
    private val messageService: MessageServicesInterface,
    private val sseServices: SseServiceInterface,
) {
    @Value("\${sse.connection.timeout}")
    private val timeout: Long = DEFAULT_TIMEOUT
    private val listeners = mutableMapOf<AuthenticatedUserInputModel, MutableSharedFlow<Message>>()
    private val scope = CoroutineScope(Dispatchers.IO)

    /**
     * Decodes the name of the channel.
     *
     * @param name The name of the channel
     * @return The decoded name
     */
    private fun decodeName(name: String): String =
        URLDecoder
            .decode(name, StandardCharsets.UTF_8.toString())

    private suspend fun sendEventToAll(message: Message) =
        listeners
            .forEach { (user, flow) ->
                when (val isUserInChannel = sseServices.isUserInChannel(message.channel.cId, user.uId)) {
                    is Success -> {
                        if (isUserInChannel.value) {
                            flow.emit(message)
                        }
                    }

                    is Failure -> {
                        // Do nothing
                    }
                }
            }

    private fun lastEventIdUpdate(
        authenticated: AuthenticatedUserInputModel,
        flow: MutableSharedFlow<Message>,
        lastId: UInt,
    ) = sseServices
        .emitAllMessages(authenticated.uId, lastId) { message ->
            scope.launch { flow.emit(message) }
        }

    @PostMapping
    @MessageSwaggerConfig.CreateMessage
    fun createMessage(
        @Valid @RequestBody message: CreateMessageInputModel,
        @Parameter(hidden = true) authenticated: AuthenticatedUserInputModel,
    ): ResponseEntity<*> {
        val response =
            messageService
                .createMessage(
                    msg = message.msg,
                    user = authenticated.uId,
                    channel = message.channel,
                )
        return when (response) {
            is Success -> {
                scope.launch {
                    sendEventToAll(response.value)
                }
                ResponseEntity.ok(MessageOutputModel.fromDomain(response.value))
            }

            is Failure -> {
                when (response.value) {
                    InvalidMessageInfo -> MessageProblem.InvalidMessageInfo.response(BAD_REQUEST)
                    ChannelNotFound -> ChannelProblem.ChannelNotFound.response(NOT_FOUND)
                    UserNotFound -> UserProblem.UserNotFound.response(NOT_FOUND)
                    else -> MessageProblem.UnableToCreateMessage.response(BAD_REQUEST)
                }
            }
        }
    }

    @GetMapping(MESSAGE_SSE_URL)
    @MessageSwaggerConfig.GetMessageEventStream
    fun getMessageEventStream(
        @Parameter(hidden = true) authenticated: AuthenticatedUserInputModel,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): SseEmitter {
        val emitter = SseEmitter(timeout)
        val lastId = request.getHeader(MESSAGE_SSE_LAST_EVENT_ID)?.toUIntOrNull()
        val flow = MutableSharedFlow<Message>()
        listeners[authenticated] = flow
        var isClosed = false
        val job =
            scope
                .launch {
                    flow
                        .onEach {
                            try {
                                emitter.send(
                                    SseEmitter
                                        .event()
                                        .name(MESSAGE)
                                        .id(it.msgId.toString())
                                        .data(MessageOutputModel.fromDomain(it)),
                                )
                            } catch (e: IOException) {
                                emitter.complete()
                            }
                        }.collect()
                }

        val keepAlive =
            scope
                .launch {
                    while (!isClosed) {
                        try {
                            delay(KEEP_ALIVE_TIMEOUT)
                            emitter
                                .send(
                                    SseEmitter
                                        .event()
                                        .name(MESSAGE_SSE_KEEP_ALIVE)
                                        .data(PING),
                                )
                        } catch (e: IOException) {
                            emitter.complete()
                        }
                    }
                }

        if (lastId != null) lastEventIdUpdate(authenticated, flow, lastId)
        emitter.onCompletion {
            listeners.remove(authenticated)
            job.cancel()
            isClosed = true
            keepAlive.cancel()
        }
        emitter.onTimeout {
            listeners.remove(authenticated)
            job.cancel()
            isClosed = true
            keepAlive.cancel()
        }
        emitter.onError {
            listeners.remove(authenticated)
            job.cancel()
            isClosed = true
            keepAlive.cancel()
        }
        response.setHeader("Cache-Control", "no-store")
        response.setHeader("Connection", "keep-alive")
        response.setHeader("Content-Type", "text/event-stream")
        return emitter
    }

    @GetMapping(MESSAGE_ID_URL)
    @MessageSwaggerConfig.GetSingleMessage
    fun getSingleMessage(
        @PathVariable msgId: UInt,
        @Parameter(hidden = true) authenticated: AuthenticatedUserInputModel,
    ): ResponseEntity<*> =
        when (val response = messageService.getMessage(msgId, authenticated.uId)) {
            is Success -> {
                ResponseEntity.ok(MessageOutputModel.fromDomain(response.value))
            }

            is Failure -> {
                MessageProblem.MessageNotFound.response(NOT_FOUND)
            }
        }

    @GetMapping(CHANNEL_MESSAGES_URL)
    @MessageSwaggerConfig.GetChannelMessages
    fun getChannelMessages(
        @PathVariable channelId: UInt,
        @RequestParam offset: UInt = OFFSET,
        @RequestParam limit: UInt = LIMIT,
        @Parameter(hidden = true) authenticated: AuthenticatedUserInputModel,
    ): ResponseEntity<*> =
        when (val response = messageService.latestMessages(channelId, authenticated.uId, offset, limit)) {
            is Success -> {
                ResponseEntity.ok(response.value.map(MessageOutputModel::fromDomain))
            }

            is Failure -> {
                MessageProblem.MessageNotFound.response(NOT_FOUND)
            }
        }

    @GetMapping(MESSAGE_TIMESTAMP_URL)
    fun getMessagesByTimeStamp(
        @PathVariable channelId: UInt,
        @RequestParam timestamp: String? = null,
        @RequestParam limit: UInt = LIMIT,
        @RequestParam isBefore: Boolean = true,
    ): ResponseEntity<*> {
        val correctTimeStamp = timestamp?.let { Timestamp.valueOf(decodeName(it)) }
        return when (val response = messageService.messagesByTimeStamp(channelId, correctTimeStamp, limit, isBefore)) {
            is Success -> {
                ResponseEntity.ok(response.value.map(MessageOutputModel::fromDomain))
            }

            is Failure -> {
                MessageProblem.MessageNotFound.response(NOT_FOUND)
            }
        }
    }

    companion object {
        const val MESSAGE_BASE_URL = "api/messages"
        const val MESSAGE_ID_URL = "/{msgId}"
        const val CHANNEL_MESSAGES_URL = "/channel/{channelId}"
        const val MESSAGE_TIMESTAMP_URL = "/channel/{channelId}/timestamp"
        const val MESSAGE_SSE_URL = "/sse"
        private const val MESSAGE_SSE_LAST_EVENT_ID = "Last-Event-ID"
        private const val MESSAGE_SSE_KEEP_ALIVE = "keep-alive"
        private const val PING = "ping"
        private const val MESSAGE = "message"
    }
}