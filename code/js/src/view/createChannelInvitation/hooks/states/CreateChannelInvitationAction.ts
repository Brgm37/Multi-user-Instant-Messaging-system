/**
 * Action to send a channel invitation
 *
 * The action can be one of the following:
 * - create: Create a channel invitation.
 * - close: Close the component.
 * - success: The invitation token was created successfully.
 * - error: An error occurred while creating the invitation token.
 *
 * @type CreateChannelInvitationAction
 * @prop type The type of the action.
 * @prop expirationDate The expiration date of the invitation token.
 * @prop maxUses The maximum number of uses for the invitation token.
 * @prop accessControl The access control for the invitation token that can be READ_ONLY or READ_WRITE.
 * @prop invitationToken The invitation token.
 * @prop error The error message.
 * @prop cId The channel ID.
 */
export type CreateChannelInvitationAction =
    { type: "create", expirationDate?: string, maxUses: number, accessControl?: "READ_ONLY" | "READ_WRITE" }
    | { type: "close", cId: string }
    | { type: "success", invitationToken: string }
    | { type: "error", error: string }
