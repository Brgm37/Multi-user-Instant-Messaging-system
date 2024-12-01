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