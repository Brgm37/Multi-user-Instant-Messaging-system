import {SignInValidationResponse} from "./SignInValidationResponse";
import {urlBuilder} from "../../utils/UrlBuilder";
import {useFetch} from "../../utils/useFetch";
import {signInValidator} from "../validation/SignInValidator";

/**
 * The service for the SignIn form.
 *
 * @method signIn
 * @method stateValidator
 */
export type SignInService = {
    /**
     * The sign in method.
     *
     * @param username
     * @param password
     * @param invitationCode
     * @param onSuccess
     * @param onError
     */
    signIn(
        username: string,
        password: string,
        invitationCode: string,
        onSuccess: (response: Response) => void,
        onError: (error: Error) => void,
    ): void

    /**
     * The state validator method.
     *
     * @param username
     * @param password
     * @param invitationCode
     */
    stateValidator(
        username: string,
        password: string,
        invitationCode: string
    ): Promise<SignInValidationResponse>
}

/**
 * The URL for the sign in API.
 */
const signInApiUrl = urlBuilder("/users/signup")

/**
 * The header for the username.
 */
const usernameHeader = "username"

/**
 * The header for the password.
 */
const passwordHeader = "password"

/**
 * The header for the invitation code.
 */
const invitationCodeHeader = "invitationCode"

/**
 * The default sign in service.
 *
 * @returns SignInService
 */
export function makeDefaultSignInService() : SignInService {
    return {
        signIn: (username, password, invitationCode, onSuccess, onError) => {
            const fetchHandler = useFetch(signInApiUrl, "POST", onSuccess, onError)
            fetchHandler.toUpdate(usernameHeader, username)
            fetchHandler.toUpdate(passwordHeader, password)
            fetchHandler.toUpdate(invitationCodeHeader, invitationCode)
            fetchHandler.toFetch()
        },
        stateValidator: signInValidator
    }
}