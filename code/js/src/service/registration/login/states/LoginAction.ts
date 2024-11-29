/**
 * The response from the validation service.
 *
 * @type LoginValidationResponse
 * @prop isUsernameValid The validation result for the username field.
 * @prop isPasswordValid The validation result for the password field.
 */
export type LoginValidationResponse = {
    isUsernameValid: string | true,
    isPasswordValid: string | true
}

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