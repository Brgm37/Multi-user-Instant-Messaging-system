/**
 * The response from the validation service.
 *
 * @type SignInValidationResponse
 *
 * @prop isUsernameValid The validation result for the username field.
 * @prop isPasswordValid The validation result for the password field.
 */
export type SignInValidationResponse = {
    isUsernameValid: string | true,
    isPasswordValid: string | true,
    isConfirmPasswordValid: string | true,
}

/**
 * The action for the signIn form.
 *
 * @type SignInAction
 *
 * @prop type The type of the action.
 */
export type SignInAction =
    { type: "edit", inputName: "username" | "password" | "confirmPassword" | "invitationCode", inputValue: string } |
    { type: "toggleVisibility", inputName: "password" | "confirmPassword" } |
    { type: "submit" } |
    { type: "success", response: Response } |
    { type: "error", message: string } |
    { type: "validation-result", response: SignInValidationResponse }