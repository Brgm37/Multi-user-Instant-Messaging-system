import React from "react";
import {InputLabel} from "../../components/InputLabel";
import {Link} from "react-router-dom";
import {InputLabelContext} from "../../components/InputLabelContext";

type InputLabelProps = {
    value: string,
    onChange: (event: React.ChangeEvent<HTMLInputElement>) => void,
    error: string
}

type LoginBaseViewProps = {
    login: InputLabelProps,
    password: InputLabelProps,
    loginState: InputLabelContext,
    onSubmit?: onSubmit,
    inputsDisabled: boolean,
    error?: string
}

type onSubmit = () => void

export function LoginBaseView(
    {login, password, loginState, onSubmit, inputsDisabled, error}: LoginBaseViewProps
): React.JSX.Element {
    return (
        <div className=" text-white flex flex-col items-center justify-center min-h-screen">
            <div className="bg-black border border-gray-700 p-8 rounded-md w-80">
                <img src={'/logo/CHImp_Logo.png'} alt={"CHImp"}/>
                <InputLabel
                    label="Username"
                    type="text"
                    disabled={inputsDisabled}
                    input={login}>
                </InputLabel>
                <InputLabel
                    label="Password"
                    type="password"
                    disabled={inputsDisabled}
                    input={password}>
                </InputLabel>
                <button
                    onClick={onSubmit} disabled={!loginState.input.isValid}
                    className="w-full bg-blue-600 text-white py-2 rounded font-medium mb-4">Log In
                </button>
                <div className="flex items-center justify-center mb-4">
                    <hr className="border-gray-700 w-full"/>
                    <span className="px-2 text-gray-500">o</span>
                    <hr className="border-gray-700 w-full"/>
                </div>
                <div className="mt-4 text-center">
                    <p className="text-gray-500 text-sm mb-4">Don´t have an account?
                        <Link
                            to={
                                {
                                    pathname: "/signIn",
                                    search: `?username=${loginState.input.username}&password=${loginState.input.password}`
                                }
                            }
                            className={"text-blue-600 hover:underline"}> Register
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