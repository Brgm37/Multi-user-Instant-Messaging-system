package com.example.appWeb.controller

import com.example.appWeb.model.dto.input.ChannelInputModel
import com.example.appWeb.model.dto.output.ChannelOutputModel
import com.example.appWeb.model.problem.Problem
import errors.ChannelError.InvalidChannelInfo
import errors.ChannelError.UserNotFound
import interfaces.ChannelServicesInterface
import jakarta.inject.Inject
import jakarta.inject.Named
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import utils.Failure
import utils.Success

@Controller
class ChannelController @Inject constructor(
	@Named("ChannelServices") private val channelService: ChannelServicesInterface
) {

	@GetMapping("/channel/{id}")
	fun getChannel(@PathVariable id: UInt) {
		when (val response = channelService.getChannel(id)) {
			is Success -> {
				ResponseEntity.ok(ChannelOutputModel.fromDomain(response.value))
			}
			is Failure -> {
				Problem.ChannelNotFound.response(NOT_FOUND)
			}
		}
	}

	@PostMapping("/channel")
	fun createChannel(
		@Valid @RequestBody channel: ChannelInputModel
	) {
		val response = channelService.createChannel(
			owner = channel.owner,
			name = channel.name,
			accessControl = channel.accessControl,
			visibility = channel.visibility
		)
		when (response) {
			is Success -> {
				ResponseEntity.ok(ChannelOutputModel.fromDomain(response.value))
			}
			is Failure -> {
				when (response.value) {
					InvalidChannelInfo -> Problem.InvalidChannelInfo.response(BAD_REQUEST)
					UserNotFound -> Problem.UserNotFound.response(NOT_FOUND)
					else -> Problem.UnableToCreateChannel.response(BAD_REQUEST)
				}
			}
		}
	}
}