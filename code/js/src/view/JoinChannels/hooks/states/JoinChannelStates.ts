
export type JoinChannelStates =
    {tag: "UseJoin"} |
    {tag: "UseJoinError", message: string} |
    {tag: "UseJoinSuccess", id: string}

