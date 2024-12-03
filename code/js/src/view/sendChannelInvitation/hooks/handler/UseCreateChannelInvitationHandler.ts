

export type UseSendChannelInvitationHandler = {

    /**
     * Create a channel invitation token.
     * @param expirationDate
     * @param maxUses
     * @param accessControl
     */
    onCreate: (expirationDate: string, maxUses: number, accessControl: "READ_ONLY" | "READ_WRITE") => void

    /**
     * Close the send channel invitation component
     */
    onClose: () => void

    /**
     * Close the error message
     */
    onErrorClose: () => void
}