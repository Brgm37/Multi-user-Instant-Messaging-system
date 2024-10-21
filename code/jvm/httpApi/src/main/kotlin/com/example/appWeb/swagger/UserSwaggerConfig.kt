package com.example.appWeb.swagger

import com.example.appWeb.model.dto.output.user.UserAuthenticatedOutputModel
import com.example.appWeb.model.dto.output.user.UserInfoOutputModel
import com.example.appWeb.model.dto.output.user.UserSignUpOutputModel
import com.example.appWeb.model.problem.UserProblem
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses

/**
 * Represents the Swagger configuration for the user endpoints in the application.
 * @see MessageSwaggerConfig
 * @see ChannelSwaggerConfig
 */
object UserSwaggerConfig {
    @Operation(summary = "Sign up a new user")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "User created successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = UserSignUpOutputModel::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid input",
                content = [Content(mediaType = "application/json")],
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [Content(mediaType = "application/json")],
            ),
        ],
    )
    annotation class SignUp

    @Operation(summary = "Get user information")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "User information retrieved successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = UserInfoOutputModel::class),
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
    annotation class GetUser

    @Operation(summary = "Log in a user")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "User logged in successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = UserAuthenticatedOutputModel::class),
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
            ApiResponse(
                responseCode = "400",
                description = "Invalid credentials",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = UserProblem::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = UserProblem::class),
                    ),
                ],
            ),
        ],
    )
    annotation class Login

    @Operation(summary = "Create an invitation")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Invitation created successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = String::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Inviter not found",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = UserProblem::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
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
    annotation class CreateInvitation

    @Operation(summary = "Log out a user")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "User logged out successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Token not found",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = UserProblem::class),
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
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
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
    annotation class Logout

    @Operation(summary = "Join a channel")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully joined the channel",
                content = [
                    Content(
                        mediaType = "application/json",
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "User or channel not found",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = UserProblem::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid or expired invitation code",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = UserProblem::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
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
    annotation class JoinChannel
}