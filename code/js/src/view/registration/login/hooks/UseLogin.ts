import {useContext, useReducer} from "react";
import {LoginState} from "./states/LoginState";
import {LoginServiceContext} from "../../../../service/registration/login/LoginServiceContext";
import {LoginHandler} from "./handler/LoginHandler";
import {setCookie} from "../../../../service/session/SetCookie";
import configJson from "../../../../../envConfig.json";
import {getExpiresIn} from "../../../../service/session/ExpiresTime";
import reduce from "./reducer/LoginReducer";

/**
 * The authentication cookie.
 */
const auth_cookie = configJson.session

/**
 * The hook for the login form.
 *
 * @returns [State, UseLoginFormHandler]
 */
export default function (): [LoginState, LoginHandler] {
    const {login} = useContext(LoginServiceContext)
    const [state, dispatch] = useReducer(reduce, {tag: "editing", isValid: false})

    const handler: LoginHandler = {
        goBack(): void {
            if (state.tag !== "error") return
            dispatch({type: "go-back"})
        },
        onSubmit(
            username: string,
            password: string
        ) {
            if (state.tag !== "editing") return
            login(
                username,
                password,
            ).then(response => {
                if (response.tag === "success") {
                    const auth = response.value
                    const validity = getExpiresIn(response.value.expirationDate)
                    setCookie(auth_cookie, auth.uId, validity)
                    dispatch({type: "success"})
                } else dispatch({type: "error", message: response.value})
            })
            dispatch({type: "submit"})
        },
        onIsValidChange(isValid: boolean) {
            if (state.tag !== "editing") return
            dispatch({type: "edit", isValid})
        }
    }
    return [state, handler]
}
