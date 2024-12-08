import {AccessControl} from "../../../../model/AccessControl";


export type UseCreateChannelInvitationHandler = {

    /**
     * Create a channel invitation token.
     * @param expirationDate
     * @param maxUses
     * @param accessControl
     */
    onCreate: (expirationDate: string, maxUses: string, accessControl: AccessControl) => void

    /**
     * Close the send channel invitation component
     */
    onClose: () => void

    /**
     * Close the error message
     */
    onErrorClose: () => void
}