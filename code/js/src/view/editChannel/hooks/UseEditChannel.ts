import {EditChannelState} from "./states/EditChannelState";
import {EditChannelHandler} from "./handler/EditChannelHandler";
import {EditChannelServiceContext} from "../../../service/editChannel/editChannelServiceContext";
import {useContext, useReducer} from "react";
import reduce from "./reducer/EditChannelReducer";
import {useParams} from "react-router-dom";


export function useEditChannel(): [EditChannelState, EditChannelHandler] {
    const context = useContext(EditChannelServiceContext)
    const {id} = useParams()
    const [state, dispatch] = useReducer(reduce, {tag: "idle"})
    const handler: EditChannelHandler = {
        loadChannel(): void {
            if (state.tag !== "idle") return
            context.loadChannel(id).then(result => {
                if (result.tag === "success") dispatch({type: "loadSuccess", channel: result.value})
                else dispatch({type: "loadError", error: result.value})
            })
            dispatch({type: "init"})
        },
        onSubmit(name: string, description: string, visibility: "PUBLIC" | "PRIVATE", icon: string): void {
            if (state.tag === "idle") return
            context.editChannel(
                id,
                name,
                description,
                visibility,
                icon
            ).then(result => {
                if (result.tag === "success") dispatch({type: "editSuccess", cId: id})
                else dispatch({type: "editError", error: result.value})
            })
            dispatch({type: "submit"})
        }
    }
    return [state, handler]
}