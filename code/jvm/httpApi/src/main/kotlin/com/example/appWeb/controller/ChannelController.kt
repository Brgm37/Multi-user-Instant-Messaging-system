package com.example.appWeb.controller

import com.example.appWeb.model.dto.input.channel.ChannelInputModel
import com.example.appWeb.model.dto.output.channel.ChannelListOutputModel
import com.example.appWeb.model.dto.output.channel.ChannelOutputModel
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
import org.springframework.web.bind.annotation.RequestParam
import utils.Failure
import utils.Success

/**
 * Represents the controller for the channel
 *
 * @param channelService The channel service
 */
@Controller
class ChannelController
	@Inject
	constructor(
		@Named("ChannelServices") private val channelService: ChannelServicesInterface,
	) {
		@GetMapping(CHANNEL_ID_URL)
		fun getChannel(
			@PathVariable channelId: UInt,
		) {
			when (val response = channelService.getChannel(channelId)) {
				is Success -> {
					ResponseEntity.ok(ChannelOutputModel.fromDomain(response.value))
				}

				is Failure -> {
					Problem.ChannelNotFound.response(NOT_FOUND)
				}
			}
		}

		@GetMapping(CHANNEL_BASE_URL)
		fun getChannels(
			@RequestParam offset: Int = 0,
			@RequestParam limit: Int = 10,
		) {
			when (val response = channelService.getChannels(offset, limit)) {
				is Failure -> {
					Problem.ChannelNotFound.response(NOT_FOUND)
				}

				is Success -> {
					ResponseEntity
						.ok(
							response
								.value
								.map(ChannelListOutputModel::fromDomain),
						)
				}
			}
		}

		@PostMapping(CHANNEL_BASE_URL)
		fun createChannel(
			@Valid @RequestBody channel: ChannelInputModel,
		) {
			val response =
				channelService.createChannel(
					owner = channel.owner,
					name = channel.name,
					accessControl = channel.accessControl,
					visibility = channel.visibility,
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

		companion object {
			/**
			 * The base URL for the channel endpoints.
			 */
			const val CHANNEL_BASE_URL = "/channels"

			/**
			 * The URL for the channel with the given id.
			 */
			const val CHANNEL_ID_URL = "$CHANNEL_BASE_URL/{channelId}"
		}
	}
