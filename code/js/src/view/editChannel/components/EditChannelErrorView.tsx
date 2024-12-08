import * as React from 'react';

/**
 * The props for the edit channel error view.
 * @param error
 * @param goBack
 */
export function EditChannelErrorView(
    {error, goBack}: { error: string, goBack: () => void }
): React.JSX.Element {
    return (
        <div className="flex items-center justify-center ">
            <h1 className="text-3xl">
                Error
            </h1>
            <p className={"font-normal border-4"}>
                {error}
            </p>
            <button
                onClick={goBack}
                className={"p-3 text-white rounded-lg mt-4 cursor-auto"}
            >
                Go back
            </button>
        </div>
    )
}