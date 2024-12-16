package com.example.appWeb.model.dto.output.user

/**
 * Data class representing the output model for creating a user invitation.
 *
 * @property invitationCode The invitation code.
 * @property expirationDate The expiration date.
 */
data class UserInvitationOutputModel(
    val invitationCode: String,
    val expirationDate: String,
) {
    companion object {
        fun fromDomain(userInvitation: model.users.UserInvitation): UserInvitationOutputModel =
            UserInvitationOutputModel(
                invitationCode = userInvitation.invitationCode.toString(),
                expirationDate = userInvitation.expirationDate.toString(),
            )
    }
}