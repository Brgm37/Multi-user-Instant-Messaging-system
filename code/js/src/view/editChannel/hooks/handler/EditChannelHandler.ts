export type EditChannelHandler = {
    onSubmit: (name: string, description: string, visibility: "PUBLIC" | "PRIVATE", icon: string) => void
    loadChannel: () => void
}