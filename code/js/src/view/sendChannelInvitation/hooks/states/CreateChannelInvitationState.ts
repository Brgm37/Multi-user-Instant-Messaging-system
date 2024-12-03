/**
 * The state of the send channel invitation component.
 *
 * @type SendChannelInvitationState
 * @prop expirationDate The expiration date of the invitation token.
 * @prop maxUses The maximum number of uses for the invitation token.
 * @prop accessControl The access control for the invitation token that can be READ_ONLY or READ_WRITE.
 * @prop invitationToken The invitation token.
 */
export type SendChannelInvitationState =
    | { tag: "editingInvitationToken", inputs: {expirationDate?: string, maxUses: number, accessControl?: "READ_ONLY" | "READ_WRITE"} }
    | { tag: "showingInvitationToken", invitationToken: string }
    | { tag: "creating" }
    | { tag: "error", error: string }
    | { tag: "closing"};
