
/**
 * The handler is used to interact with the CreateUserInvitation component.
 *
 * The handler is used to interact with the CreateUserInvitation component.
 * It provides methods to create an app invitation token, close the component, and close the error message.
 *
 * @method onCreate Create an app invitation token.
 * @method onClose Close the send app invitation view.
 * @method onErrorClose Close the error message.
 */
export type UseCreateUserInvitationHandler = {
    onCreate: (expirationDate: string) => void
    onClose: () => void
    onErrorClose: () => void
}