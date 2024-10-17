package com.example.appWeb.controller

import com.example.appWeb.controller.ChannelController.Companion.CHANNEL_BASE_URL
import com.example.appWeb.controller.ChannelController.Companion.CHANNEL_ID_URL
import com.example.appWeb.controller.UserController.Companion.USER_BASE_URL
import com.example.appWeb.controller.UserController.Companion.USER_ID_URL
import com.example.appWeb.model.dto.input.message.CreateMessageInputModel
import com.example.appWeb.model.dto.output.message.MessageOutputModel
import com.example.appWeb.model.problem.ChannelProblem
import com.example.appWeb.model.problem.MessageProblem
import com.example.appWeb.model.problem.UserProblem
import errors.MessageError.ChannelNotFound
import errors.MessageError.InvalidMessageInfo
import errors.MessageError.UserNotFound
import interfaces.MessageServicesInterface
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import utils.Failure
import utils.Success
import java.sql.Timestamp
import java.time.LocalDateTime

@Controller
class MessageController(
    private val messageService: MessageServicesInterface,
) {
    @GetMapping(MESSAGE_CREATE_URL)
    fun createMessage(
        @Valid @RequestBody message: CreateMessageInputModel,
    ) {
        val response =
            messageService
                .createMessage(
                    msg = message.msg,
                    user = message.user,
                    channel = message.channel,
                    creationTime = Timestamp.valueOf(LocalDateTime.now()).toString(),
                )
        when (response) {
            is Success -> {
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
    fun getSingleMessage(
        @PathVariable userId: UInt,
        @PathVariable msgId: UInt,
    ) {
        when (val response = messageService.getMessage(msgId, userId)) {
            is Success -> {
                ResponseEntity.ok(MessageOutputModel.fromDomain(response.value))
            }

            is Failure -> {
                MessageProblem.MessageNotFound.response(NOT_FOUND)
            }
        }
    }

    @GetMapping(CHANNEL_MESSAGES_URL)
    fun getChannelMessages(
        @PathVariable userId: UInt,
        @PathVariable channelId: UInt,
        @RequestParam offset: Int,
        @RequestParam limit: Int,
    ) {
        when (val response = messageService.latestMessages(channelId, userId, offset, limit)) {
            is Success -> {
                ResponseEntity.ok(response.value.map(MessageOutputModel::fromDomain))
            }

            is Failure -> {
                MessageProblem.MessageNotFound.response(NOT_FOUND)
            }
        }
    }

    companion object {
        const val MESSAGE_CREATE_URL = "$USER_BASE_URL/$CHANNEL_BASE_URL/messages"
        const val MESSAGE_ID_URL = "$USER_ID_URL/$CHANNEL_BASE_URL/messages/{msgId}"
        const val CHANNEL_MESSAGES_URL = "$USER_ID_URL/$CHANNEL_ID_URL/messages"
    }
}