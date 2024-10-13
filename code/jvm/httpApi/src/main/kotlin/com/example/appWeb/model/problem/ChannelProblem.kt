package com.example.appWeb.model.problem

import java.net.URI

private const val PROBLEM_URI_PATH =
    "https://github.com/isel-leic-daw/2024-daw-leic52d-im-i52d-2425-g04/tree/main/docs/problems/channel"

sealed class ChannelProblem(
    typeUri: URI,
) : Problem(typeUri) {
    data object ChannelNotFound : Problem(URI("$PROBLEM_URI_PATH/channel-not-found"))

    data object InvalidChannelInfo : Problem(URI("$PROBLEM_URI_PATH/invalid-channel-info"))

    data object UnableToCreateChannel : Problem(URI("$PROBLEM_URI_PATH/unable-to-create-channel"))

    data object InvalidChannelVisibility : Problem(URI("$PROBLEM_URI_PATH/invalid-channel-visibility"))

    data object InvalidChannelAccessControl : Problem(URI("$PROBLEM_URI_PATH/invalid-channel-access-control"))
}
