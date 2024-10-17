package com.example.appWeb.model.problem

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.net.URI

private const val MEDIA_TYPE = "application/problem+json"
private const val PROBLEM_URI_PATH = "https://github.com/isel-leic-daw/2024-daw-leic52d-im-i52d-2425-g04/tree/main/docs"

sealed class Problem(
    typeUri: URI,
) {
    fun response(status: HttpStatus): ResponseEntity<Any> =
        ResponseEntity
            .status(status)
            .header("Content-Type", MEDIA_TYPE)
            .body(this)
}