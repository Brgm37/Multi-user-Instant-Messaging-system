import * as React from 'react';
import {InputLabelContext} from "./InputLabelContext";


export function InputLabel({label, type}: {label: string, type: string}) {
    const {value, onChange, error} = React.useContext(InputLabelContext)
    return (
        <div>
            <label>{label}</label>
            <br/>
            <input type={type} value={value} onChange={onChange}/>
            {error && <span>{error}</span>}
        </div>
    )
}