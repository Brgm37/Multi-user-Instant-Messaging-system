import * as React from 'react';
import {useEffect, useState} from "react";

const TIMEOUT = 500;

/**
 * The search bar for finding channels.
 * @param handleChange The function to call when the search value changes.
 */
export default function (
    {handleChange}: { handleChange: (value: string) => void }
): React.JSX.Element {
    const [searchValue, setSearchValue] = useState<string>("")

    useEffect(() => {
        const timeout = setTimeout(() => {
            handleChange(searchValue)
        }, TIMEOUT);
        return () => clearTimeout(timeout);
    }, [searchValue]);

    const onChange = (event: React.ChangeEvent<HTMLInputElement>) => setSearchValue(event.target.value);

    return (
        <input className= {"bg-black text-white p-2 rounded ml-auto"}
               type="text"
               value={searchValue}
               onChange={onChange}
               placeholder={"Search for channels"}
        />
    )
}

