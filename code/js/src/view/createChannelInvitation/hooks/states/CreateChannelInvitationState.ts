/**
 * The state of the send channel invitation component.
 *
 * The state can be one of the following:
 * - editingInvitationToken: The user is editing the invitation token.
 * - showingInvitationToken: The invitation token is being displayed.
 * - creating: The invitation token is being created.
 * - error: An error occurred while creating the invitation token.
 * - closing: The component is closing.
 *
 * @type CreateChannelInvitationState
 * @prop tag The state of the component.
 * @prop inputs The inputs for the invitation token.
 * @prop expirationDate The expiration date of the invitation token.
 * @prop maxUses The maximum number of uses for the invitation token.
 * @prop accessControl The access control for the invitation token that can be READ_ONLY or READ_WRITE.
 * @prop invitationToken The invitation token.
 * @prop error The error message.
 * @prop cId The channel ID.
 */
export type CreateChannelInvitationState =
    | { tag: "editingInvitationToken", inputs: {expirationDate?: string, maxUses: number, accessControl?: "READ_ONLY" | "READ_WRITE"} }
    | { tag: "showingInvitationToken", invitationToken: string }
    | { tag: "creating" }
    | { tag: "error", error: string }
    | { tag: "closing", cId: string};
