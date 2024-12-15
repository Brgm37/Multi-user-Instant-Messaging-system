import * as React from 'react';

/**
 * The props for the input bar component.
 */
type InputBarProps = {
    value: string;
    onChange: (value: string) => void;
    className?: string;
    placeholder?: string;
};


/**
 * An input bar component.
 * @param value The value of the input bar.
 * @param onChange The function to call when the input bar changes.
 * @param className The class name for the input bar.
 * @param placeholder The placeholder for the input bar.
 * @returns JSX.Element
 */
export function InputBar({value, onChange, className, placeholder}: InputBarProps): React.JSX.Element {
    const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        onChange(event.target.value);
    }
    return (
        <input className= {className}
            type="text"
            value={value}
            onChange={handleChange}
            placeholder={placeholder}
        />
    )
}