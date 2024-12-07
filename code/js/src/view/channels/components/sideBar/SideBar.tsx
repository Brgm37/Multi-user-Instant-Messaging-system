import * as React from "react";
import InfiniteScroll from "../../../components/infiniteScroll/InfiniteScroll";
import { useContext, useEffect, useState } from "react";
import { ChannelsServiceContext } from "../../../../service/channels/ChannelsServiceContext";
import { Channel } from "../../../../model/Channel";
import {Link, useNavigate} from "react-router-dom";
import {IoIosAdd} from "react-icons/io";

const TIMEOUT = 500;

export function Sidebar(): React.JSX.Element {
    const [channels, setChannels] = useState<Channel[]>([]);
    const [search, setSearch] = useState<string>("");
    const [isInputVisible, setInputVisible] = useState<boolean>(false);
    const { findChannelsByName } = useContext(ChannelsServiceContext);
    const navigate = useNavigate();

    const onSearchChange = (value: React.ChangeEvent<HTMLInputElement>) => setSearch(value.target.value);

    useEffect(() => {
        const timeout = setTimeout(() => {
            findChannelsByName(search, 0, 15)
                .then(response => {
                    if (response.tag === "success") setChannels(response.value);
                });
        }, TIMEOUT);
        return () => clearTimeout(timeout);
    }, [search]);

    useEffect(() => {
        const timeout = setTimeout(() => {
            const c = channels.find(channel => channel.name === search);
            if (c) {
                navigate(`/channels/${c.id}`);
                clearSearch();
            }
        }, TIMEOUT);
        return () => clearTimeout(timeout);
    }, [search]);

    const toggleInput = () => {
        setInputVisible(!isInputVisible);
    };

    const clearSearch = () => {
        setSearch("");
        setInputVisible(false);
    }

    return (
        <div className="relative w-20 bg-gray-900 flex flex-col items-center py-4 space-y-4">
            <div
                className="w-14 h-14 overflow-hidden rounded-full transition-transform duration-300 ease-in-out transform hover:scale-110 hover:shadow-lg cursor-pointer flex items-center justify-center"
                onClick={toggleInput}
            >
                <img src="/sideBarIcons/black_search.webp" alt="Search"
                     className="w-full h-full object-cover object-center"/>
            </div>

            <div
                className={`absolute w-40 top-0 left-20 bg-black text-white p-2 rounded transition-all duration-300 ease-in-out ${isInputVisible ? 'opacity-100 z-10' : 'opacity-0 z-0 pointer-events-none'}`}>
                <input
                    type="text"
                    value={search}
                    onChange={onSearchChange}
                    placeholder="Search in your channels"
                    className="bg-black text-white text-center w-full h-6 p-2 text-xs"
                    list="suggestions"
                />
                <datalist id="suggestions" className={"bg-pink-700 text-white p-2 rounded ml-auto w-full "}>
                    {channels.map(channel => (<option key={channel.id} value={channel.name}/>))}
                </datalist>
            </div>

            <Link to={"/channels/findChannels"} className={"relative group"}>
                <div
                    className="w-14 h-14 overflow-hidden rounded-full transition-transform duration-300 ease-in-out transform hover:scale-110 hover:shadow-lg">
                    <img src={"/sideBarIcons/findChannels.png"} alt={"findChannels"} className="w-full h-full object-cover object-center"/>
                </div>
                <div
                    className="absolute left-16 top-1/2 transform -translate-y-1/2 z-50 bg-black text-white text-sm font-bold px-2 py-1 rounded-md opacity-0 group-hover:opacity-100 transition-opacity duration-300 tooltip-arrow pointer-events-none group-hover:pointer-events-auto"
                    style={{width: "max-content"}}
                >Find Channels
                </div>
            </Link>

            <Link to={"/channels/createChannel"} className={"relative group"}>
                <div
                    className="w-14 h-14 overflow-hidden rounded-full transition-transform duration-300 ease-in-out transform hover:scale-110 hover:shadow-lg">
                    <IoIosAdd className="w-full h-full object-cover object-center bg-black"/>
                </div>
                <div
                    className="absolute left-16 top-1/2 transform -translate-y-1/2 z-50 bg-black text-white text-sm font-bold px-2 py-1 rounded-md opacity-0 group-hover:opacity-100 transition-opacity duration-300 tooltip-arrow pointer-events-none group-hover:pointer-events-auto"
                    style={{width: "max-content"}}
                >
                    Create Channel
                </div>
            </Link>

            <div className="w-12 h-0.5 bg-gray-700"></div>

            <div className={"w-full scrollable-sidebar"}>
                <InfiniteScroll
                    className="scrollable-sidebar"
                    scrollStyle="scrollable-sidebar space-y-2"
                />
            </div>
        </div>
    );
}