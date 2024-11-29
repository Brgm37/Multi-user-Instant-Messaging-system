/**
 * The Handler for the login form.
 */
export type UseLoginFormHandler = {
    /**
     * The function to call when the username changes.
     * @param username The new username.
     * @returns void
     */
    onUsernameChange: (username: string) => void,
    /**
     * The function to call when the password changes.
     * @param password The new password.
     * @returns void
     */
    onPasswordChange: (password: string) => void,

    /**
     * The function to call when the user clicks on the password visibility button.
     *
     * @returns void
     */
    togglePasswordVisibility: () => void,

    /**
     * The function to call when the user clicks on the submit button.
     *
     * @returns void
     */
    onSubmit: () => void,
}