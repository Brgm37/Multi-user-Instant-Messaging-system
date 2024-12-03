/**
 * The handler for the SignIn form.
 *
 * @type SingInFormHandler
 *
 * @prop onUsernameChange The handler for the username change event.
 * @prop onPasswordChange The handler for the password change event.
 * @prop onConfirmPasswordChange The handler for the password confirmation change event.
 * @prop onInvitationCodeChange The handler for the invitation code change event.
 */
export type SingInFormHandler = {
    /**
     * The handler for the username change event.
     * @param username
     */
    onUsernameChange: (username: string) => void,
    /**
     * The handler for the password change event.
     * @param password
     */
    onPasswordChange: (password: string) => void,
    /**
     * The handler for the password confirmation change event.
     * @param confirmPassword
     */
    onConfirmPasswordChange: (confirmPassword: string) => void,
    /**
     * The handler for the invitation code change event.
     * @param invitationCode
     */
    onInvitationCodeChange: (invitationCode: string) => void,

    /**
     * The handler for the form submission event.
     */
    onSubmit: () => void

}