package com.example.appWeb.controller

import interfaces.ChannelServicesInterface
import jakarta.inject.Inject
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class ChannelController(
//	private val channelService: ChannelServicesInterface
) {

	@GetMapping("/channel/{id}")
	fun getChannel(@PathVariable id: String) {
		TODO()
	}
}