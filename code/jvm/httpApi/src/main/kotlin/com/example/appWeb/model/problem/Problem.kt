package com.example.appWeb.model.problem

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.net.URI

private const val MEDIA_TYPE = "application/problem+json"
private const val PROBLEM_URI_PATH = "https://github.com/isel-leic-daw/2024-daw-leic52d-im-i52d-2425-g04/tree/main/docs"

sealed class Problem(
	typeUri: URI
) {
	val type = typeUri.toString()
	val title = type.split("/").last()

	fun response(status: HttpStatus): ResponseEntity<Any> =
		ResponseEntity
			.status(status)
			.header("Content-Type", MEDIA_TYPE)
			.body(this)

	data object ChannelNotFound : Problem(URI("$PROBLEM_URI_PATH/channel-not-found"))
	data object InvalidChannelInfo : Problem(URI("$PROBLEM_URI_PATH/invalid-channel-info"))
	data object UserNotFound : Problem(URI("$PROBLEM_URI_PATH/user-not-found"))
	data object UnableToCreateChannel : Problem(URI("$PROBLEM_URI_PATH/unable-to-create-channel"))
}