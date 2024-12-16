import * as React from 'react';

/**
 * The input label props.
 */
type InputLabelProps = {
    value: string,
    onChange: (event: React.ChangeEvent<HTMLInputElement>) => void,
    error: string,
}

/**
 * The input label component.
 * @param label
 * @param type
 * @param disabled
 * @param input
 */
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