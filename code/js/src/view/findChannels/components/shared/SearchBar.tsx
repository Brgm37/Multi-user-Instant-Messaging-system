import * as React from 'react';

/**
 * The props for the search bar component.
 */
type SearchBarProps = {
    value: string;
    onChange: (value: string) => void;
};

/**
 * A search bar component.
 * @param value The value of the search bar.
 * @param onChange The function to call when the search bar changes.
 * @returns JSX.Element
 */
export function SearchBar(
    {value, onChange}: SearchBarProps
): React.JSX.Element {
    return (
        <div>
            <input
                type="text"
                value={value}
                onChange={(event) => onChange(event.target.value)}
                placeholder="Search for channels"
            />
        </div>
    )
}