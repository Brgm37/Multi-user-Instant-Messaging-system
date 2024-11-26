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
import jakarta.validation.Valid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import model.messages.Message
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
import java.util.concurrent.TimeUnit

/**
 * The default limit for the message list.
 */
private const val OFFSET = 0u

/**
 * The default offset for the message list.
 */
private const val LIMIT = 100u

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
    // TODO("Change globalMessages to a database")
    private val globalMessages: MutableSharedFlow<Message> = MutableSharedFlow(replay = 1000)
    private val listeners = mutableMapOf<AuthenticatedUserInputModel, MutableSharedFlow<Message>>()
    private val scope = CoroutineScope(Dispatchers.IO)

    private suspend fun sendEventToAll(message: Message) {
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
    }

    private fun listenersInit(
        user: AuthenticatedUserInputModel,
        flow: MutableSharedFlow<Message>,
        lastEventId: UInt,
    ) {
        scope
            .launch {
                var count = globalMessages.replayCache.size
                globalMessages
                    .filter {
                        when (val result = sseServices.isUserInChannel(it.channel.cId, user.uId)) {
                            is Success -> result.value
                            is Failure -> false
                        }
                    }.filter {
                        val msgId = checkNotNull(it.msgId) { "Message id is null" }
                        msgId > lastEventId
                    }.takeWhile { count-- > 0 }
                    .collect(flow::emit)
            }
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
                    globalMessages.emit(response.value)
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
    fun getMessageEventStream(
        @Parameter(hidden = true) authenticated: AuthenticatedUserInputModel,
        request: HttpServletRequest,
    ): SseEmitter {
        val emitter = SseEmitter(TimeUnit.HOURS.toMillis(1)) // TODO: Change to config file.
        val lastId = request.getHeader(MESSAGE_SSE_LAST_EVENT_ID)?.toUIntOrNull()
        val flow = MutableSharedFlow<Message>()
        listeners[authenticated] = flow
        if (lastId != null) listenersInit(authenticated, flow, lastId)
        val scope =
            scope
                .launch {
                    if (lastId != null) {
                        flow
                            .filterNotNull()
                            .filter { it.msgId?.let { msgId -> msgId > lastId } ?: true }
                            .onEach {
                                emitter.send(
                                    SseEmitter
                                        .event()
                                        .id(it.msgId.toString())
                                        .data(MessageOutputModel.fromDomain(it)),
                                )
                            }.collect()
                    } else {
                        flow
                            .filterNotNull()
                            .onEach {
                                emitter.send(
                                    SseEmitter
                                        .event()
                                        .id(it.msgId.toString())
                                        .data(MessageOutputModel.fromDomain(it)),
                                )
                            }.collect()
                    }
                }
        emitter.onCompletion {
            listeners.remove(authenticated)
            scope.cancel()
        }
        emitter.onTimeout {
            listeners.remove(authenticated)
            scope.cancel()
        }
        emitter.onError {
            listeners.remove(authenticated)
            scope.cancel()
        }
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

    companion object {
        const val MESSAGE_BASE_URL = "api/messages"
        const val MESSAGE_ID_URL = "/{msgId}"
        const val CHANNEL_MESSAGES_URL = "/channel/{channelId}"
        const val MESSAGE_SSE_URL = "/sse"
        private const val MESSAGE_SSE_LAST_EVENT_ID = "Last-Event-ID"
    }
}