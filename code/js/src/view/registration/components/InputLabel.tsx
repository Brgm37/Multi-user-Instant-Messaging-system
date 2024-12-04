import * as React from 'react';

type InputLabelProps = {
    value: string,
    onChange: (event: React.ChangeEvent<HTMLInputElement>) => void,
    error: string,
}

export function InputLabel(
    {label, type, disabled, input}: {label: string, type: string, disabled: boolean, input: InputLabelProps}
) {
    return (
        <div>
            <input
                placeholder={label}
                type={type}
                value={input.value}
                onChange={input.onChange}
                disabled={disabled}
                className={"w-full p-2 mb-4 bg-gray-900 border border-gray-700 rounded text-sm"}
            />
            {input.error &&
                <div className="flex items-center justify-center bg-black mb-4">
                    <div className="text-center text-red-600 text-sm">
                        <p>{input.error}</p>
                    </div>
                </div>
            }
        </div>
    )
}