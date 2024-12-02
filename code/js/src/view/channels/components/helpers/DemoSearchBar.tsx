import * as React from 'react';
import {ChannelsMenuContext} from "../ChannelsMenuContext";
import {useContext} from "react";

/**
 * The props for the search bar component.
 */
type SearchBarProps = {
    onSubmit: (value: string) => void;
};

/**
 * A search bar component.
 * @returns JSX.Element
 */
export function DemoSearchBar(): React.JSX.Element {
    const {onSearch, onCancelSearch, isSearching} = useContext(ChannelsMenuContext)
    const [value, setValue] = React.useState('');
    const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => setValue(event.target.value);
    const handleSubmit = (event: React.FormEvent<HTMLButtonElement>) => {
        event.preventDefault();
        onSearch(value);
    }
    return (
        <div>
            <input
                type="text"
                value={value}
                onChange={handleChange}
                placeholder="Search for channels"
                disabled={isSearching}
            />
            <button
                type="submit"
                onClick={handleSubmit}
                disabled={isSearching}
            >
                Search
            </button>
            <button
                onClick={onCancelSearch}
                type="button"
            >
                Clear
            </button>
        </div>
    )
}