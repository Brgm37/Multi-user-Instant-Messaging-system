/**
 * Action to send a CreateUserInvitation
 *
 * @type CreateUserInvitationAction
 * @prop type The type of the action.
 * @prop expirationDate The expiration date of the invitation token.
 * @prop invitationCode The invitation code.
 * @prop message The error message.
 */

export type CreateUserInvitationAction =
    { type: "create", expirationDate?: string }
    | { type: "close" }
    | { type: "success", invitationCode: string }
    | { type: "error", message: string }
    | { type: "reset" }