import * as React from 'react';
import {ChannelsMenuContext} from "../ChannelsMenuContext";
import {useContext, useEffect} from "react";
import {Channel} from "../../../../model/Channel";
import {ChannelsServiceContext} from "../../../../service/channels/ChannelsServiceContext";

/**
 * The offset for the search.
 */
const OFFSET = 0;

/**
 * The limit for the search.
 */
const LIMIT = 15;

/**
 * The minimum search length.
 */
const MINIMUM_SEARCH_LENGTH = 3;

/**
 * A search bar component.
 * @returns JSX.Element
 */
export function PartialNameSearchBar(): React.JSX.Element {
    const {onSearch, onCancelSearch, isSearching} = useContext(ChannelsMenuContext)
    const {findChannelsByName} = useContext(ChannelsServiceContext)
    const [list, setList] = React.useState<Channel[]>([]);
    const [value, setValue] = React.useState('');
    const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => setValue(event.target.value);
    useEffect(() => {
        const timeout = setTimeout(() => {
        if (value.length >= MINIMUM_SEARCH_LENGTH) {
            findChannelsByName(value, OFFSET, LIMIT)
                .then((result) => {
                    if (result.tag === "success") {
                        setList(result.value);
                    } else {
                        setList([]);
                    }
                })
        }
        }, 500);
        return () => clearTimeout(timeout);
    }, [value]);
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
                list="suggestions"
            />
            <datalist id="suggestions">
                {list.map((channel) => (
                    <option key={channel.id} value={channel.name}/>
                ))}
            </datalist>
            <button
                type="submit"
                onClick={handleSubmit}
                disabled={isSearching || value.length < MINIMUM_SEARCH_LENGTH}
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