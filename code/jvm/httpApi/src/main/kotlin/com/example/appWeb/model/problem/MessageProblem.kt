package com.example.appWeb.model.problem

import java.net.URI

private const val PROBLEM_URI_PATH =
    "https://github.com/isel-leic-daw/2024-daw-leic52d-im-i52d-2425-g04/tree/main/docs"

sealed class MessageProblem (
    typeUri: URI,
) : Problem(typeUri) {
    data object MessageNotFound : Problem(URI("$PROBLEM_URI_PATH/message-not-found"))

    data object InvalidMessageInfo : Problem(URI("$PROBLEM_URI_PATH/invalid-channel-message"))

    data object UnableToCreateMessage : Problem(URI("$PROBLEM_URI_PATH/unable-to-create-message"))

}