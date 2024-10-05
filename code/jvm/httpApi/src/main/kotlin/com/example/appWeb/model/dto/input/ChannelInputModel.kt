package com.example.appWeb.model.dto.input

import jakarta.annotation.PostConstruct
import model.AccessControl
import model.Visibility

data class ChannelInputModel(
	val name: String,
	val visibility: String,
	val accessControl: String,
) {
	@PostConstruct
	fun validate() {
		require(name.isNotBlank()) { "Channel name cannot be blank" }
		require(visibility.uppercase() in Visibility.entries.map(Visibility::name)) { "Invalid visibility" }
		require(accessControl.uppercase() in AccessControl.entries.map(AccessControl::name)) {
			"Invalid access control"
		}
	}
}
