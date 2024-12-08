export type EditChannelHandler = {
    onSubmit: (description: string, visibility: "PUBLIC" | "PRIVATE", icon: string) => void
    loadChannel: () => void,
    goBack: () => void
}