import {usernameValidation} from "./UsernameValidation"
import {passwordValidation} from "./PasswordValidation"
import {SignInValidationResponse} from "../signIn/aux/SignInAction";

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
): Promise<SignInValidationResponse> {
    return Promise.resolve(
        {
            isUsernameValid: usernameValidation(username),
            isPasswordValid: passwordValidation(password),
            isConfirmPasswordValid: password === confirmPassword ? true : "Passwords do not match",
        }
    )
}