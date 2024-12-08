/**
 * The Handler for the login form.
 *
 * The handler is used to interact with the login form.
 * It provides methods to submit the form, change the validity of the form, and go back to the editing state.
 *
 * @method onSubmit The function to call when the user clicks on the submit button.
 * @method onIsValidChange The function to call when the user changes the username.
 * @method goBack The function to call to go back to the editing state.
 */
export type LoginHandler = {
    onSubmit(
        username: string,
        password: string,
    ): void,
    onIsValidChange(
        isValid: boolean
    ) : void,
    goBack(): void
}