package com.example.appWeb.controller

import com.example.appWeb.model.dto.input.user.AuthenticatedUserInputModel
import com.example.appWeb.model.dto.input.user.CreateUserInvitationInputModel
import com.example.appWeb.model.dto.input.user.UserLogInInputModel
import com.example.appWeb.model.dto.input.user.UserSignUpInputModel
import com.example.appWeb.model.dto.output.user.UserAuthenticatedOutputModel
import com.example.appWeb.model.dto.output.user.UserInfoOutputModel
import com.example.appWeb.model.dto.output.user.UserInvitationOutputModel
import com.example.appWeb.model.problem.UserProblem
import com.example.appWeb.swagger.UserSwaggerConfig
import errors.UserError
import interfaces.UserServicesInterface
import io.swagger.v3.oas.annotations.Parameter
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import utils.Failure
import utils.Success

/**
 * Represents the controller for the user
 *
 * @property userService The user service
 */
@RestController
@RequestMapping(UserController.USER_BASE_URL)
class UserController(
    private val userService: UserServicesInterface,
) {
    @PostMapping(SIGNUP_URL)
    @UserSwaggerConfig.SignUp
    fun signUp(
        @Valid @RequestBody user: UserSignUpInputModel,
        res: HttpServletResponse,
    ): ResponseEntity<*> {
        val response =
            userService.createUser(
                username = user.username,
                password = user.password,
                invitationCode = user.invitationCode,
            )
        return when (response) {
            is Success -> {
                val auth = response.value
                val cookie = Cookie(AUTH_COOKIE, auth.token.toString())
                cookie.path = "/api"
                cookie.isHttpOnly = true
                cookie.maxAge = auth.expirationDateInInt
                res.addCookie(cookie)
                ResponseEntity.ok(UserAuthenticatedOutputModel.fromDomain(response.value))
            }

            is Failure -> {
                when (response.value) {
                    UserError.UsernameAlreadyExists -> UserProblem.UsernameAlreadyExists.response(BAD_REQUEST)
                    UserError.InviterNotFound -> UserProblem.InviterNotFound.response(BAD_REQUEST)
                    UserError.InvitationCodeIsInvalid -> UserProblem.InvitationCodeIsInvalid.response(BAD_REQUEST)
                    UserError.InvitationCodeHasExpired -> UserProblem.InvitationCodeHasExpired.response(BAD_REQUEST)
                    else -> UserProblem.UnableToCreateUser.response(INTERNAL_SERVER_ERROR)
                }
            }
        }
    }

    @GetMapping(USER_ID_URL)
    @UserSwaggerConfig.GetUser
    fun getUser(
        @PathVariable userId: UInt,
        @Parameter(hidden = true) authenticated: AuthenticatedUserInputModel,
    ): ResponseEntity<*> =
        when (val response = userService.getUser(userId)) {
            is Success -> {
                ResponseEntity.ok(UserInfoOutputModel.fromDomain(response.value))
            }

            is Failure -> {
                UserProblem.UserNotFound.response(NOT_FOUND)
            }
        }

    @PostMapping(LOGIN_URL)
    @UserSwaggerConfig.Login
    fun login(
        @Valid @RequestBody user: UserLogInInputModel,
        res: HttpServletResponse,
    ): ResponseEntity<*> =
        when (val response = userService.login(user.username, user.password)) {
            is Success -> {
                val auth = response.value
                val cookie = Cookie(AUTH_COOKIE, auth.token.toString())
                cookie.path = "/api"
                cookie.isHttpOnly = true
                cookie.maxAge = auth.expirationDateInInt
                res.addCookie(cookie)
                ResponseEntity.ok(UserAuthenticatedOutputModel.fromDomain(auth))
            }

            is Failure -> {
                when (response.value) {
                    UserError.UserNotFound -> UserProblem.UserNotFound.response(NOT_FOUND)
                    UserError.PasswordIsInvalid -> UserProblem.PasswordIsInvalid.response(BAD_REQUEST)
                    UserError.UnableToCreateToken -> UserProblem.UnableToCreateToken.response(INTERNAL_SERVER_ERROR)
                    else -> UserProblem.UnableToLogin.response(INTERNAL_SERVER_ERROR)
                }
            }
        }

    @PostMapping(INVITATION_URL)
    @UserSwaggerConfig.CreateInvitation
    fun createInvitation(
        @Valid @RequestBody expirationDate: CreateUserInvitationInputModel,
        @Parameter(hidden = true) authenticated: AuthenticatedUserInputModel,
    ): ResponseEntity<*> =
        when (val response = userService.createInvitation(authenticated.uId, expirationDate.expirationDate)) {
            is Success -> {
                ResponseEntity.ok(UserInvitationOutputModel.fromDomain(response.value))
            }

            is Failure -> {
                when (response.value) {
                    UserError.InviterNotFound -> UserProblem.InviterNotFound.response(NOT_FOUND)
                    else -> UserProblem.UnableToCreateInvitation.response(INTERNAL_SERVER_ERROR)
                }
            }
        }

    @DeleteMapping(LOGOUT_URL)
    @UserSwaggerConfig.Logout
    fun logout(
        @Parameter(hidden = true) authenticated: AuthenticatedUserInputModel,
    ): ResponseEntity<*> =
        when (val response = userService.logout(authenticated.token, authenticated.uId)) {
            is Success -> {
                ResponseEntity.ok().build<Any>()
            }

            is Failure -> {
                when (response.value) {
                    UserError.TokenNotFound -> UserProblem.TokenNotFound.response(BAD_REQUEST)
                    UserError.UserNotFound -> UserProblem.UserNotFound.response(NOT_FOUND)
                    UserError.UnableToDeleteToken -> UserProblem.UnableToLogout.response(INTERNAL_SERVER_ERROR)
                    else -> UserProblem.UnableToLogout.response(INTERNAL_SERVER_ERROR)
                }
            }
        }

    companion object {
        /**
         * The base URL for the user endpoints.
         */
        const val USER_BASE_URL = "/api/users"

        /**
         * The URL for the user with the given id.
         */
        const val USER_ID_URL = "/{userId}"

        /**
         * The URL for the user to make an invitation to make a login.
         */
        const val INVITATION_URL = "/invitation"

        /**
         * The URL for the login.
         */
        const val LOGIN_URL = "/login"

        /**
         * The URL for the signup.
         */
        const val SIGNUP_URL = "/signup"

        /**
         * The URL for the logout.
         */
        const val LOGOUT_URL = "/logout"

        const val AUTH_COOKIE = "auth-token"
    }
}