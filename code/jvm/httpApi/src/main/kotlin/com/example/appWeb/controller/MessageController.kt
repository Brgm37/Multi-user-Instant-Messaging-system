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
import io.swagger.v3.oas.annotations.Parameter
import jakarta.validation.Valid
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
import utils.Failure
import utils.Success
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

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
) {
    private val listeners = mutableMapOf<UInt, MutableList<(String) -> Unit>>()
    private val lock = ReentrantLock()
    private var connectionCounter = 0

    private val executor =
        Executors
            .newScheduledThreadPool(1)
            .also {
                it.scheduleAtFixedRate({ keepAlive() }, 2, 2, TimeUnit.SECONDS)
            }

    private fun keepAlive() =
        lock.withLock {
            if (listeners.isNotEmpty()) {
//                sendEventToAll()
            }
        }

    private fun sendEventToAll(channelId: UInt, msg: String) {
        listeners[channelId]?.forEach {
            try {
                it(msg)
            } catch (ex: Exception) {
                //do nothing
            }
        }
    }

    private fun removeListener(channelId: UInt, listener: (String) -> Unit) =
        lock.withLock {
            listeners[channelId]?.remove(listener)
        }

    fun addListener(channelId: UInt, listener: (String) -> Unit) =
        lock.withLock {
            listeners[channelId]?.add(listener)
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
                sendEventToAll(message.channel, message.msg)
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
    }
}