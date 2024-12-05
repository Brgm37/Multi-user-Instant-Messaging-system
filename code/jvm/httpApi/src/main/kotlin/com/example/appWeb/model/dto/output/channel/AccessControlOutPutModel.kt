package com.example.appWeb.model.dto.output.channel

import model.channels.AccessControl

/**
 * The DTO to represent an AccessControl
 */
data class AccessControlOutPutModel(
    val accessControl: String,
) {
    companion object {
        fun fromAccessControl(accessControl: AccessControl?): AccessControlOutPutModel =
            AccessControlOutPutModel(accessControl?.name ?: "null")
    }
}