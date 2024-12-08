/**
 * The handler for the registration form.
 *
 * The handler is used to interact with the registration form.
 * It provides methods to set the form to valid or invalid, go back to the editing state, and submit the form.
 *
 * @method setIsValid Sets the form to valid or invalid.
 * @method goBack Goes back to editing state.
 * @method onSubmit Submits the form.
 */
export type RegisterHandler = {
    setIsValid(value: boolean): void
    goBack(): void
    onSubmit(
        username: string,
        password: string,
        invitationCode: string,
    ): void
}