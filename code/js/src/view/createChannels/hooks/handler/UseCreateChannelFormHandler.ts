export type UseCreateChannelFormHandler = {
    /**
     * The function to call when the name changes.
     * @param name The new name.
     * @returns void
     */
    onNameChange: (name: string) => void,
    /**
     * The function to call when the visibility changes.
     * @param visibility The new visibility.
     * @returns void
     */
    onVisibilityChange: (visibility: string) => void,
    /**
     * The function to call when the access changes.
     * @param access The new access.
     * @returns void
     */
    onAccessChange: (access: string) => void,
    /**
     * The function to call when the user clicks on the submit button.
     *
     * @returns void
     */
    onSubmit: () => void,
}