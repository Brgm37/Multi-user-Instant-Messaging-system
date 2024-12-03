import {CreateChannelsState} from "./states/createChannelsState";
import {CreateChannelsAction} from "./states/createChannelsAction";

function reduce(state: CreateChannelsState, action: CreateChannelsAction): CreateChannelsState {
    switch (state.tag) {
        case "editing":
            switch (action.type) {
                case "edit": {
                    
                }
                default:
                    throw Error("Invalid action")
            }
    }
}