import {Channel} from "../../../../model/Channel";

export type EditChannelAction =
    | { type: "init" }
    | { type: "loadSuccess", channel: Channel }
    | { type: "loadError", error: string }
    | { type: "editSuccess", cId: string }
    | { type: "editError", error: string }
    | { type: "submit" }
    | { type: "redirect" }
    | { type: "closeError" }