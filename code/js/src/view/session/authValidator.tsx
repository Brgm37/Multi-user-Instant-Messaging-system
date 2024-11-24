import * as React from "react"
import {useNavigate} from "react-router-dom"
import {useCookies} from "react-cookie"

function isNotValid(token: string): boolean {
    // TODO: Implement isNotValid function
    return token === "undefined" || token === "null" || token === ""
}

/**
 * 
 */
const TOKEN = "token"

export function AuthValidator({children}: {children: React.ReactNode}): React.ReactElement {
    const navigate = useNavigate()
    const [cookies] = useCookies([TOKEN])
    const token = cookies.token

    React.useEffect(() => {
        if (!token || isNotValid(token)) {
            navigate("/login")
        }
    },[token, navigate])

    return <>{children}</>
}