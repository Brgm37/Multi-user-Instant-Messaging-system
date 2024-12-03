import {SignInValidationResponse} from "../../../../../service/registration/signIn/SignInValidationResponse";

/**
 * The action for the signIn form.
 *
 * @type SignInAction
 *
 * @prop type The type of the action.
 */
export type SignInAction =
    { type: "edit", inputName: "username" | "password" | "confirmPassword" | "invitationCode", inputValue: string } |
    { type: "submit" } |
    { type: "success" } |
    { type: "error", message: string } |
    { type: "validation-result", response: SignInValidationResponse }