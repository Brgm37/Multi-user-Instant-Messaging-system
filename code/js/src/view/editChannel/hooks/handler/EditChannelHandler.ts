/**
 * The handler is used to interact with the EditChannel component.
 *
 * The handler is used to interact with the EditChannel component.
 * It provides methods to submit the channel, load the channel, and go back to the previous state.
 *
 * @method onSubmit - The function to call when the user clicks on the submit button.
 * @method loadChannel - The function to call when the component is loaded.
 * @method goBack - The function to call when the user clicks on the back button.
 */
export type EditChannelHandler = {
    onSubmit: (description: string, visibility: "PUBLIC" | "PRIVATE", icon: string) => void
    loadChannel: () => void,
    goBack: () => void
}