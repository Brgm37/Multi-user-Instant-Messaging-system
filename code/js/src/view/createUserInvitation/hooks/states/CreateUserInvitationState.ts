/**
 * Represents the state of the CreateUserInvitation component.
 *
 * @type CreateUserInvitationState
 * @prop tag The state of the component.
 * @prop expirationDate The expiration date of the invitation token.
 * @prop invitationCode The invitation code.
 * @prop message The error message.
 */


export type CreateUserInvitationState =
    { tag: "editingInvitationCode", expirationDate?: string}
    | { tag: "showingInvitationCode", invitationCode: string }
    | { tag: "creating" }
    | { tag: "error", message: string }
    | { tag: "closing"}
