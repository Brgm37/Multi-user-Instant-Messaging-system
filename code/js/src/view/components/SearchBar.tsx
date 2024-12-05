import * as React from 'react';

/**
 * The props for the search bar component.
 */
type SearchBarProps = {
    value: string;
    onChange: (value: string) => void;
    className?: string;
    placeholder?: string;
};

/**
 * A search bar component.
 * @param value The value of the search bar.
 * @param isSearching Whether the search bar is currently searching.
 * @param onChange The function to call when the search bar changes.
 * @param className The class name for the search bar.
 * @returns JSX.Element
 */
export function SearchBar({value, onChange, className, placeholder}: SearchBarProps): React.JSX.Element {
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