package com.example.appWeb.swagger

import com.example.appWeb.model.dto.output.channel.ChannelListOutputModel
import com.example.appWeb.model.dto.output.channel.ChannelOutputModel
import com.example.appWeb.model.problem.ChannelProblem
import com.example.appWeb.model.problem.UserProblem
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses

/**
 * Swagger configuration for the channel controller
 *
 * @see UserSwaggerConfig
 * @see MessageSwaggerConfig
 */
object ChannelSwaggerConfig {
    @Operation(summary = "Get a channel by ID")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Channel retrieved successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ChannelOutputModel::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Channel not found",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ChannelProblem::class),
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
    annotation class GetChannel

    @Operation(summary = "Get a list of channels")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Channels retrieved successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ChannelListOutputModel::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Channels not found",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ChannelProblem::class),
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
    annotation class GetChannels

    @Operation(summary = "Create a new channel")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Channel created successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ChannelOutputModel::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid channel information",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ChannelProblem::class),
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
    @Parameter(
        name = "Authorization",
        description = "Authorization token",
        required = true,
        schema = Schema(type = "UUID"),
        `in` = ParameterIn.HEADER,
        example = "Bearer 123e4567-e89b-12d3-a456-426614174000",
    )
    annotation class CreateChannel

    @Operation(summary = "Create a channel invitation")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Channel invitation created successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid channel information",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ChannelProblem::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Channel not found",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ChannelProblem::class),
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
    annotation class CreateChannelInvitation
}