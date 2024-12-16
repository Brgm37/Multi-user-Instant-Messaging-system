import {JoinChannelState} from "./states/JoinChannelState";
import reduce from "./reducer/JoinChannelReducer";
import {useContext, useReducer} from "react";
import {UseJoinChannelHandler} from "./handler/UseJoinChannelHandler";
import {JoinChannelServiceContext} from "../../../service/joinChannel/JoinChannelServiceContext";

/**
 * The error message to display when an error occurs while joining a channel.
 */
const ERROR_MESSAGE = "An error occurred while joining the channel. Please try again later.";

/**
 * The hook for the JoinChannel component.
 */
export function useJoinChannel(): [JoinChannelState, UseJoinChannelHandler] {
    const [state, dispatch] = useReducer(reduce, { tag: "UseJoin" })
    const service = useContext(JoinChannelServiceContext)

    const handler: UseJoinChannelHandler = {
        onJoin: (joinCode: string) => {
            service.joinChannel(joinCode).then(response => {
                if (response.tag === "success") {
                    dispatch({ type: "success", id: response.value.id })
                } else {
                    dispatch({ type: "error", message: ERROR_MESSAGE })
                }
            })
        },
        onClose: () => {
            dispatch({ type: "close" })
        }
    }
    return [state, handler]
}