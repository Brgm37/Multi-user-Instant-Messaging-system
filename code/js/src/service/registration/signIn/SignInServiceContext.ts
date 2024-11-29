import {SignInValidationResponse} from "./SignInValidationResponse";
import {Context, createContext} from "react";

/**
 * The context for the sign in service.
 *
 * @method signIn
 * @method stateValidator
 */
export interface SignInServiceContext {
    /**
     * The sign in method.
     *
     * @param username
     * @param password
     * @param invitationCode
     */
    signIn(
        username: string,
        password: string,
        invitationCode: string,
    ): Promise<true | string>

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

const defaultSignInServiceContext: SignInServiceContext = {
    signIn: (username, password, invitationCode) => {
        return new Promise<true | string>((resolve, reject) => {
            reject(new Error("Not implemented"))
        })
    },
    stateValidator: (username, password, invitationCode) => {
        return new Promise<SignInValidationResponse>((resolve, reject) => {
            reject(new Error("Not implemented"))
        })
    }
}

export const SignInServiceContext: Context<SignInServiceContext> =
    createContext(defaultSignInServiceContext)

// import {SignInValidationResponse} from "./SignInValidationResponse";
// import {urlBuilder} from "../../utils/UrlBuilder";
// import {useFetch} from "../../utils/useFetch";
// import {signInValidator} from "../validation/SignInValidator";
//
// /**
//  * The service for the SignIn form.
//  *
//  * @method signIn
//  * @method stateValidator
//  */
// export type SignInService = {
//     /**
//      * The sign in method.
//      *
//      * @param username - the username for the sign in.
//      * @param password - the password for the sign in.
//      * @param invitationCode - the invitation code for the sign in.
//      * @param onComplete - the callback function when the sign in is complete.
//      */
//     signIn(
//         username: string,
//         password: string,
//         invitationCode: string,
//         onComplete: (response: true | string) => void,
//     ): void
//
//     /**
//      * The state validator method.
//      *
//      * @param username
//      * @param password
//      * @param invitationCode
//      */
//     stateValidator(
//         username: string,
//         password: string,
//         invitationCode: string
//     ): Promise<SignInValidationResponse>
// }
//
// /**
//  * The URL for the sign in API.
//  */
// const signInApiUrl = urlBuilder("/users/signup")
//
// /**
//  * The header for the username.
//  */
// const usernameHeader = "username"
//
// /**
//  * The header for the password.
//  */
// const passwordHeader = "password"
//
// /**
//  * The header for the invitation code.
//  */
// const invitationCodeHeader = "invitationCode"
//
// /**
//  * The default sign in service.
//  *
//  * @returns SignInService
//  */
// export function makeDefaultSignInService() : SignInService {
//     return {
//         signIn: (username, password, invitationCode, onComplete) => {
//             const fetchHandler = useFetch(signInApiUrl, "POST", response => {
//                 if (response.ok) {
//                     onComplete(true)
//                 } else {
//                     response.json().then(data => {
//                         onComplete(data.message)
//                     })
//                 }
//             })
//             fetchHandler.toUpdate(usernameHeader, username)
//             fetchHandler.toUpdate(passwordHeader, password)
//             fetchHandler.toUpdate(invitationCodeHeader, invitationCode)
//             fetchHandler.toFetch()
//         },
//         stateValidator: signInValidator
//     }
// }