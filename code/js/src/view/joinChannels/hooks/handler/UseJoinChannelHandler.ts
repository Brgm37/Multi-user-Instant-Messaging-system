/**
 * The handler for the join channel component.
 *
 * The handler is used to interact with the join channel component.
 * It provides methods to join a channel and close the join channel component.
 *
 * @method onJoin Join a channel.
 * @method onClose Close the join channel component.
 */
export type UseJoinChannelHandler = {
    onJoin: (joinCode: string) => void
    onClose: () => void
}