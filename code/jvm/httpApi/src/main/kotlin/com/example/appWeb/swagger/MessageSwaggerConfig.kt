package com.example.appWeb.swagger

import com.example.appWeb.model.dto.output.message.MessageOutputModel
import com.example.appWeb.model.problem.ChannelProblem
import com.example.appWeb.model.problem.MessageProblem
import com.example.appWeb.model.problem.UserProblem
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses

/**
 * Represents the Swagger configuration for the messages endpoints in the application.
 * Represents the Swagger configuration for the message endpoints in the application.
 * @see UserSwaggerConfig
 * @see ChannelSwaggerConfig
 */
object MessageSwaggerConfig {
    @Operation(summary = "Create a new message")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Message created successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = MessageOutputModel::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid message information",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = MessageProblem::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Channel or user not found",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ChannelProblem::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "500",
                description = "Unable to create message",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = MessageProblem::class),
                    ),
                ],
            ),
        ],
    )
    @Parameter(
        name = "Authorization",
        description = "Authorization token",
        required = true,
        schema = Schema(type = "UUID"),
        `in` = ParameterIn.HEADER,
        example = "Bearer 123e4567-e89b-12d3-a456-426614174000",
    )
    annotation class CreateMessage

    @Operation(summary = "Get a single message")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Message retrieved successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = MessageOutputModel::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Message not found",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = MessageProblem::class),
                    ),
                ],
            ),
        ],
    )
    @Parameter(
        name = "Authorization",
        description = "Authorization token",
        required = true,
        schema = Schema(type = "UUID"),
        `in` = ParameterIn.HEADER,
        example = "Bearer 123e4567-e89b-12d3-a456-426614174000",
    )
    annotation class GetSingleMessage

    @Operation(summary = "Get messages from a channel")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Messages retrieved successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = MessageOutputModel::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Messages not found",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = MessageProblem::class),
                    ),
                ],
            ),
        ],
    )
    @Parameter(
        name = "Authorization",
        description = "Authorization token",
        required = true,
        schema = Schema(type = "UUID"),
        `in` = ParameterIn.HEADER,
        example = "Bearer 123e4567-e89b-12d3-a456-426614174000",
    )
    annotation class GetChannelMessages

    @Operation(summary = "Get messages event stream")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Message event stream started successfully",
                content = [
                    Content(
                        mediaType = "text/event-stream",
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = MessageProblem::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "User not found",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = UserProblem::class),
                    ),
                ],
            ),
        ],
    )
    annotation class GetMessageEventStream
}