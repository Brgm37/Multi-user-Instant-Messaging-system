import {RegisterState} from "./states/RegisterState";
import {useContext, useReducer} from "react";
import {RegisterHandler} from "./handler/RegisterHandler";
import {setCookie} from "../../../../service/session/SetCookie";
import configJson from "../../../../../envConfig.json";
import {getExpiresIn} from "../../../../service/session/ExpiresTime";
import reduce from "./reducer/RegisterReducer";
import {RegisterServiceContext} from "../../../../service/registration/register/RegisterServiceContext";

/**
 * The authentication cookie.
 */
const auth_cookie = configJson.session

/**
 * The hook to handle the registration.
 *
 * @return The state and handler of the registration.
 */
export default function(): [RegisterState, RegisterHandler] {
    const {signIn} = useContext(RegisterServiceContext)
    const [state, dispatch] =
        useReducer(
            reduce,
            {tag: "editing", isValid: false}
        )
    const handler: RegisterHandler = {
        goBack(): void {
            if (state.tag !== "error") return
            dispatch({type: "go-back"})
        },
        setIsValid(value): void {
            if (state.tag !== "editing") return
            dispatch({type: "edit", isValid: value})
        },
        onSubmit: (
            username,
            password,
            invitationCode
        ) => {
            if (state.tag !== "editing") return
            signIn(username, password, invitationCode)
                .then(response => {
                    if (response.tag === "success") {
                        const auth = response.value
                        const validity = getExpiresIn(response.value.expirationDate)
                        setCookie(auth_cookie, auth.uId, validity)
                        dispatch({type: "success"})
                    } else dispatch({type: "error", message: response.value})
                })
            dispatch({type: "submit"})
        }
    }
    return [state, handler]
}