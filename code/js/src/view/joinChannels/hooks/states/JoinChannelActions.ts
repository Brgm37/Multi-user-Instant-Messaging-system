/**
 * The actions for the JoinChannel component.
 *
 * The actions can be one of the following:
 * - success: The user has successfully joined the channel.
 * - error: An error occurred while joining the channel.
 * - close: The component is closing.
 *
 * @type JoinChannelActions
 * @prop type The type of the action.
 * @prop id The id of the channel.
 * @prop message The error message.
 */
export type JoinChannelActions =
    { type: "success", id: string } |
    { type: "error", message: string } |
    { type: "close"}
