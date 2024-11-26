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
            <label>{label}</label>
            <br/>
            <input type={type} value={input.value} onChange={input.onChange} disabled={disabled}/>
            {input.error && <span>{input.error}</span>}
        </div>
    )
}