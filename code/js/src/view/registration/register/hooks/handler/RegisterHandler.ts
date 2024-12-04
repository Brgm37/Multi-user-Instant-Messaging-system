/**
 * The handler for the registration form.
 *
 * @type RegisterHandler
 *
 * @method setIsValid Sets the form to valid or invalid.
 *
 * @method goBack Goes back to editing state.
 *
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