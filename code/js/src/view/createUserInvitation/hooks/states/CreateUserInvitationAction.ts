/**
 * Action to send a CreateUserInvitation
 *
 * The action can be of the following types:
 * - create: Create a new invitation.
 * - close: Close the invitation dialog.
 * - success: The invitation was created successfully.
 * - error: The invitation was not created successfully.
 * - reset: Reset the state.
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