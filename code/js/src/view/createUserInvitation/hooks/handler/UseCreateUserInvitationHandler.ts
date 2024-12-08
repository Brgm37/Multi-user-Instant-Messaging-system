
export type UseCreateUserInvitationHandler = {
    /**
     * Create an app invitation token.
     * @param expirationDate
     */
    onCreate: (expirationDate: string) => void

    /**
     * Close the send app invitation component
     */
    onClose: () => void

    /**
     * Close the error message
     */
    onErrorClose: () => void
}