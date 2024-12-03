import {InputLabel} from "../../components/InputLabel";
import {Link} from "react-router-dom";
import * as React from "react";

type SignInBaseViewProps = {
    signIn: InputLabelProps,
    password: InputLabelProps,
    confirmPassword: InputLabelProps,
    invitationCode: InputLabelProps,
    inputsDisabled: boolean,
    isValid: boolean,
    error?: string,
    onSubmit?: () => void
}

type InputLabelProps = {
    value: string,
    onChange: (event: React.ChangeEvent<HTMLInputElement>) => void,
    error: string
}

export function SignInBaseView(
    {signIn, password, confirmPassword, invitationCode, inputsDisabled, error, isValid, onSubmit}: SignInBaseViewProps
) {
    return (
        <div className=" text-white flex flex-col items-center justify-center min-h-screen">
            <div className="bg-black border border-gray-700 p-8 rounded-md w-80">
                <img src={'/logo/CHImp_Logo.png'} alt={"CHImp"}/>
                <InputLabel
                    label="Username"
                    type="text"
                    disabled={inputsDisabled}
                    input={signIn}>
                </InputLabel>
                <InputLabel
                    label="Password"
                    type="password"
                    disabled={inputsDisabled}
                    input={password}>
                </InputLabel>
                <InputLabel
                    label="Confirm Password"
                    type={"password"}
                    disabled={inputsDisabled}
                    input={confirmPassword}>
                </InputLabel>
                <InputLabel
                    label="Invitation Code"
                    type="text"
                    disabled={inputsDisabled}
                    input={invitationCode}>
                </InputLabel>
                <button
                    onClick={onSubmit} disabled={!isValid}
                    className="w-full bg-blue-600 text-white py-2 rounded font-medium mb-4">Register
                </button>
                <div className="flex items-center justify-center mb-4">
                    <hr className="border-gray-700 w-full"/>
                    <span className="px-2 text-gray-500">o</span>
                    <hr className="border-gray-700 w-full"/>
                </div>
                <div className="mt-4 text-center">
                    <p className="text-gray-500 text-sm mb-4">Already have an account?
                        <Link
                            to={
                                {
                                    pathname: "/login",
                                    search: `?username=${signIn.value}&password=${password.value}`
                                }
                            }
                            className={"text-blue-600 hover:underline"}> Login
                        </Link>
                    </p>
                </div>
                {error &&
                    <div className="flex items-center justify-center bg-black mb-4">
                        <div className="text-center text-red-600 text-sm">
                            <p>{error}</p>
                        </div>
                    </div>
                }
            </div>
        </div>
    )
}