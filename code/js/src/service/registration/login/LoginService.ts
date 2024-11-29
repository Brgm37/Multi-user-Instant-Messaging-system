import {useFetch} from "../../utils/useFetch";
import {urlBuilder} from "../../utils/UrlBuilder";
import {LoginValidationResponse} from "./LoginValidationResponse";
import {loginValidator} from "../validation/LoginValidator";

/**
 * Interface for the login service
 *
 * @method login
 * @method stateValidator
 */
export type LoginService = {
    /**
     * The login method.
     * @param username
     * @param password
     * @param onSuccess
     * @param onError
     */
    login(
        username: string,
        password: string,
        onSuccess: (response: Response) => void,
        onError: (error: Error) => void,
    ): void

    stateValidator(username: string, password: string): Promise<LoginValidationResponse>
}

/**
 * The URL for the login API.
 */
const loginApiUrl = urlBuilder("/users/login")

/**
 * The header for the username.
 */
const usernameHeader = "username"

/**
 * The header for the password.
 */
const passwordHeader = "password"

/**
 * The default login service.
 *
 * @returns LoginService
 */
export function makeDefaultLoginService() : LoginService {
    const fetchHandler = useFetch(loginApiUrl, "POST")
    return {
        login: (username, password, onSuccess, onError) => {
            fetchHandler.toUpdate(usernameHeader, username)
            fetchHandler.toUpdate(passwordHeader, password)
            fetchHandler.onSuccessChange(onSuccess)
            fetchHandler.onErrorChange(onError)
            fetchHandler.toFetch()
        },
        stateValidator: loginValidator
    }
}