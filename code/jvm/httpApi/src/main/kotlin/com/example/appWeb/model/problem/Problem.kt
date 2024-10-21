package com.example.appWeb.model.problem

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.net.URI

private const val MEDIA_TYPE = "application/problem+json"

/**
 * Represents a Problem.
 *
 * @param typeUri the URI of the problem type.
 */
sealed class Problem(
    typeUri: URI,
) {
    @Suppress("unused")
    val type = typeUri.toString()

    @Suppress("unused")
    val title = typeUri.toString().split("/").last()

    fun response(status: HttpStatus): ResponseEntity<Any> =
        ResponseEntity
            .status(status)
            .header("Content-Type", MEDIA_TYPE)
            .body(this)
}