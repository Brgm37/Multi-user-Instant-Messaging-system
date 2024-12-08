/**
 * Represents the state of the CreateUserInvitation component.
 *
 * The state can be in one of the following states:
 * - editingInvitationCode: The state where the user is editing the invitation code.
 * - showingInvitationCode: The state where the invitation code is displayed.
 * - creating: The state where the invitation is being created.
 * - error: The state where an error occurred.
 * - closing: The state where the component is closing.
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
