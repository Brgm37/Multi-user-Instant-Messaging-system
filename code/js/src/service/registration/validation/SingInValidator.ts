import {SingInValidationResponse} from "../SingInService"
import {usernameValidation} from "./UsernameValidation"
import {passwordValidation} from "./PasswordValidation"

/**
 * Validate the username, password and confirm password
 *
 * @param username
 * @param password
 * @param confirmPassword
 */
export function singInValidator(
    username: string,
    password: string,
    confirmPassword: string
): Promise<SingInValidationResponse> {
    return Promise.resolve(
        {
            isUsernameValid: usernameValidation(username),
            isPasswordValid: passwordValidation(password),
            isConfirmPasswordValid: password === confirmPassword ? true : "Passwords do not match",
        }
    )
}