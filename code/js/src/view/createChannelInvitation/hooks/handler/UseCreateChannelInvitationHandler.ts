import {AccessControl} from "../../../../model/AccessControl";

/**
 * The create channel invitation handler.
 *
 * The handler is used to interact with the create channel invitation component.
 * It provides methods to create a channel invitation token, close the component, and close the error message.
 *
 * @method onCreate Create a channel invitation token.
 * @method onClose Close the send channel invitation component.
 * @method onErrorClose Close the error message.
 */
export type UseCreateChannelInvitationHandler = {
    onCreate: (expirationDate: string, maxUses: string, accessControl: AccessControl) => void
    onClose: () => void
    onErrorClose: () => void
}