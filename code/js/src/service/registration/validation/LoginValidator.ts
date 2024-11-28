import {usernameValidation} from "./UsernameValidation";
import {passwordValidation} from "./PasswordValidation";
import {LoginValidationResponse} from "../login/LoginValidationResponse";

/**
 * Validate the username and password
 *
 * @param username
 * @param password
 */
export function loginValidator(username: string,password: string): Promise<LoginValidationResponse> {
    return Promise.resolve(
        {
            isUsernameValid: usernameValidation(username),
            isPasswordValid: passwordValidation(password),
        }
    )
}