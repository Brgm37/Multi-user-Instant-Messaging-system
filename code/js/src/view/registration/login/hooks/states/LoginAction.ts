import {LoginValidationResponse} from "../../../../../service/registration/login/LoginValidationResponse";

/**
 * The action for the login form.
 *
 * @type LoginAction
 * @prop type The type of the action.
 */
export type LoginAction =
    { type: "edit", inputName: "username" | "password", inputValue: string } |
    { type: "toggleVisibility" } |
    { type: "submit" } |
    { type: "success", response: Response } |
    { type: "error", message: string } |
    { type: "validation-result", response: LoginValidationResponse }