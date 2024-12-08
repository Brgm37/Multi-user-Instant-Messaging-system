/**
 * The state of the JoinChannel component.
 *
 * The state can be one of the following:
 * - UseJoin: The user is joining the channel.
 * - UseJoinError: An error occurred while joining the channel.
 * - UseJoinSuccess: The user successfully joined the channel.
 * - UseJoinClose: The component is closing.
 *
 * @type JoinChannelStates
 * @prop tag The state of the component.
 * @prop message The error message.
 * @prop id The channel ID.
 */
export type JoinChannelStates =
    {tag: "UseJoin"} |
    {tag: "UseJoinError", message: string} |
    {tag: "UseJoinSuccess", id: string} |
    {tag: "UseJoinClose"}

