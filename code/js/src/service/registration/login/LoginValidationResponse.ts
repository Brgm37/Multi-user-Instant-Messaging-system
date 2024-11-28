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