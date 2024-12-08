import React from "react";
import {Link} from "react-router-dom";
import {InputFormContext} from "../../components/InputFormContext";
import {InputLabelContext} from "../../components/input/InputContext";
import Input from "../../components/input/Input";
import LoadingIcon from "../../../components/LoadingIcon";

/**
 * The view for the login page.
 */
type LoginBaseViewProps = {
    inputsDisabled: boolean,
    isValid: boolean,
    error?: string
    onSubmit?: () => void,
}

/**
 * The view for the login page.
 */
export function LoginBaseView(
    {onSubmit, inputsDisabled, error, isValid}: LoginBaseViewProps
): React.JSX.Element {
    const context = React.useContext(InputFormContext)
    return (
        <div className=" text-white flex flex-col items-center justify-center min-h-screen">
            <div className="bg-black border border-gray-700 p-8 rounded-md w-80">
                <img src={'/logo/CHImp_Logo.png'} alt={"CHImp"}/>
                <InputLabelContext.Provider value={context.username}>
                    <Input isDisabled={inputsDisabled} labelName={"Username"} type={"text"}/>
                </InputLabelContext.Provider>
                <InputLabelContext.Provider value={context.password}>
                    <Input isDisabled={inputsDisabled} labelName={"Password"} type={"password"}/>
                </InputLabelContext.Provider>
                <button
                    onClick={onSubmit} disabled={!isValid}
                    className="w-full bg-blue-600 text-white py-2 rounded font-medium mb-4">Log In
                </button>
                <div className="flex items-center justify-center mb-4">
                    <hr className="border-gray-700 w-full"/>
                    <span className="px-2 text-gray-500">o</span>
                    <hr className="border-gray-700 w-full"/>
                </div>
                {inputsDisabled && (
                    <div className="flex items-center justify-center">
                        <LoadingIcon/>
                    </div>
                )}
                <div className="mt-4 text-center">
                    <p className="text-gray-500 text-sm mb-4">Don´t have an account?
                        <Link
                            to={
                                {
                                    pathname: "/register/singIn",
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