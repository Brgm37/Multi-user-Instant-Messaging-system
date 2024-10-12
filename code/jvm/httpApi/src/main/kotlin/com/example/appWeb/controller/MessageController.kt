package com.example.appWeb.controller

import com.example.appWeb.controller.ChannelController.Companion.CHANNEL_BASE_URL
import com.example.appWeb.controller.ChannelController.Companion.CHANNEL_ID_URL
import interfaces.MessageServicesInterface
import jakarta.inject.Inject
import jakarta.inject.Named
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class MessageController
@Inject
constructor(
	@Named("MessageService") private val messageService: MessageServicesInterface,
) {
	@GetMapping(MESSAGE_ID_URL)
	fun getSingleMessage(
		@PathVariable msgId: UInt,
	) {
		TODO()
	}

	@GetMapping(MESSAGE_CREATE_URL)
	fun createMessage() {
		TODO()
	}

	companion object {
		const val MESSAGE_CREATE_URL = "$CHANNEL_BASE_URL/messages"
		const val MESSAGE_ID_URL = "$CHANNEL_BASE_URL/messages/{msgId}"
		const val CHANNEL_MESSAGES_URL = "$CHANNEL_ID_URL/messages"
	}
}
