import {JoinChannelStates} from "./states/JoinChannelStates";
import {JoinChannelActions} from "./states/JoinChannelActions";
import {useContext, useReducer} from "react";
import {UseJoinChannelHandler} from "./handler/UseJoinChannelHandler";
import {JoinChannelServiceContext} from "../../../service/joinChannel/JoinChannelServiceContext";

const ERROR_MESSAGE = "An error occurred while joining the channel. Please try again later.";

function reduce (state: JoinChannelStates, action: JoinChannelActions): JoinChannelStates {
    switch (state.tag) {
        case "UseJoin":
            switch (action.type) {
                case "success":
                    return { tag: "UseJoinSuccess", id: action.id }
                case "error":
                    return { tag: "UseJoinError", message: action.message }
                case "close":
                    return { tag: "UseJoinClose" }
                default:
                    throw new Error("Invalid action")
            }
        case "UseJoinError":
            switch (action.type) {
                case "close":
                    return { tag: "UseJoinClose" }
                default:
                    throw new Error("Invalid action")
            }
        case "UseJoinClose":
            throw new Error("Final state reached")
        case "UseJoinSuccess":
            throw new Error("Final state reached")
    }
}

export function useJoinChannel(): [JoinChannelStates, UseJoinChannelHandler] {
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