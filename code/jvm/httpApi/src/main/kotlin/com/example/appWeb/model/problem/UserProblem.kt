package com.example.appWeb.model.problem

import java.net.URI

private const val PROBLEM_URI_PATH =
    "https://github.com/isel-leic-daw/2024-daw-leic52d-im-i52d-2425-g04/tree/main/docs/problems/user"

sealed class UserProblem(
    typeUri: URI,
) : Problem(typeUri) {
    data object InvalidUserInfo : Problem(URI("$PROBLEM_URI_PATH/invalid-user-info"))

    data object UserAlreadyExists : Problem(URI("$PROBLEM_URI_PATH/user-already-exists"))

    data object UsernameAlreadyExists : Problem(URI("$PROBLEM_URI_PATH/username-already-exists"))

    data object UserNotFound : Problem(URI("$PROBLEM_URI_PATH/user-not-found"))

    data object UnableToCreateUser : Problem(URI("$PROBLEM_URI_PATH/unable-to-create-user"))

    data object UnableToJoinChannel : Problem(URI("$PROBLEM_URI_PATH/unable-to-join-channel"))

    data object InvalidInviter : Problem(URI("$PROBLEM_URI_PATH/invalid-inviter"))

    data object InviterNotFound : Problem(URI("$PROBLEM_URI_PATH/inviter-not-found"))

    data object InvitationCodeHasExpired : Problem(URI("$PROBLEM_URI_PATH/invitation-code-has-expired"))

    data object InvitationCodeIsInvalid : Problem(URI("$PROBLEM_URI_PATH/invitation-code-is-invalid"))

    data object InvitationCodeMaxUsesReached : Problem(URI("$PROBLEM_URI_PATH/invitation-code-max-uses-reached"))

    data object Unauthorized : Problem(URI("$PROBLEM_URI_PATH/unauthorized"))

    data object UnableToLogin : Problem(URI("$PROBLEM_URI_PATH/unable-to-login"))

    data object PasswordIsInvalid : Problem(URI("$PROBLEM_URI_PATH/password-is-invalid"))

    data object UnableToCreateToken : Problem(URI("$PROBLEM_URI_PATH/unable-to-create-token"))

    data object TokenNotFound : Problem(URI("$PROBLEM_URI_PATH/token-not-found"))

    data object UnableToLogout : Problem(URI("$PROBLEM_URI_PATH/unable-to-logout"))

    data object UnableToCreateInvitation : Problem(URI("$PROBLEM_URI_PATH/unable-to-create-invitation"))
}