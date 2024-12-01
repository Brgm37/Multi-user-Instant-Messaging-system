import * as React from 'react';

/**
 * The props for the search bar component.
 */
type SearchBarProps = {
    value: string;
    isSearching: boolean;
    onChange: (value: string) => void;
};

/**
 * A search bar component.
 * @param value The value of the search bar.
 * @param isSearching Whether the search bar is currently searching.
 * @param onChange The function to call when the search bar changes.
 * @returns JSX.Element
 */
export function SearchBar({value, onChange}: SearchBarProps): React.JSX.Element {
    const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        onChange(event.target.value);
    }
    return (
        <div>
            <input
                type="text"
                value={value}
                onChange={handleChange}
                placeholder="Search for channels"
            />
        </div>
    )
}