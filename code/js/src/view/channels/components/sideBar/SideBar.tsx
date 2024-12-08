import * as React from "react";
import InfiniteScroll from "../../../components/infiniteScroll/InfiniteScroll";
import {useContext, useEffect, useState} from "react";
import {ChannelsServiceContext} from "../../../../service/channels/ChannelsServiceContext";
import {Channel} from "../../../../model/Channel";
import {Link, useNavigate} from "react-router-dom";
import {IoIosAdd} from "react-icons/io";
import {IoInformationCircleSharp, IoLogOutOutline} from "react-icons/io5";
import {FaSearch} from "react-icons/fa";
import {RiUserAddFill} from "react-icons/ri";
import {TbDoorEnter} from "react-icons/tb";

/**
 * The timeout for the search.
 */
const TIMEOUT = 500;

/**
 * The sidebar component.
 */
export function Sidebar(): React.JSX.Element {
    const [channels, setChannels] = useState<Channel[]>([]);
    const [search, setSearch] = useState<string>("");
    const [isInputVisible, setInputVisible] = useState<boolean>(false);
    const {findChannelsByName, logout} = useContext(ChannelsServiceContext);
    const navigate = useNavigate();

    const onSearchChange = (value: React.ChangeEvent<HTMLInputElement>) => setSearch(value.target.value);

    useEffect(() => {
        const timeout = setTimeout(() => {
            if (search.length === 0) return;
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

            <div className={"relative group cursor-pointer"}>
                <div
                    className="w-12 h-12 bg-black overflow-hidden rounded-full transition-transform duration-300 ease-in-out transform hover:scale-110 hover:shadow-lg cursor-pointer flex items-center justify-center"
                    onClick={toggleInput}
                >
                    <FaSearch className="w-6 h-6" color={"white"}/>
                </div>
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

                <Link to={"/channels/createUserInvitation"} className={"relative group"}>
                    <div
                        className="w-12 h-12 bg-black overflow-hidden rounded-full transition-transform duration-300 ease-in-out transform hover:scale-110 hover:shadow-lg cursor-pointer flex items-center justify-center">
                        <RiUserAddFill className="w-6 h-6"/>
                    </div>
                    <div
                        className="absolute left-16 top-1/2 transform -translate-y-1/2 z-50 bg-black text-white text-sm font-bold px-2 py-1 rounded-md opacity-0 group-hover:opacity-100 transition-opacity duration-300 tooltip-arrow pointer-events-none group-hover:pointer-events-auto"
                        style={{width: "max-content"}}
                    >Create User Invitation
                    </div>
                </Link>

                <Link to={"/channels/joinChannel"} className={"relative group"}>
                    <div
                        className="w-12 h-12 bg-black overflow-hidden rounded-full transition-transform duration-300 ease-in-out transform hover:scale-110 hover:shadow-lg cursor-pointer flex items-center justify-center">
                        <TbDoorEnter className="w-6 h-6"/>
                    </div>
                    <div
                        className="absolute left-16 top-1/2 transform -translate-y-1/2 z-50 bg-black text-white text-sm font-bold px-2 py-1 rounded-md opacity-0 group-hover:opacity-100 transition-opacity duration-300 tooltip-arrow pointer-events-none group-hover:pointer-events-auto"
                        style={{width: "max-content"}}
                    >Join Channel
                    </div>
                </Link>

                <Link to={"/channels/findChannels"} className={"relative group"}>
                    <div
                        className="w-12 h-12 overflow-hidden rounded-full transition-transform duration-300 ease-in-out transform hover:scale-110 hover:shadow-lg">
                        <img src={"/sideBarIcons/findChannels.png"} alt={"findChannels"}
                             className="w-full h-full object-cover object-center"/>
                    </div>
                    <div
                        className="absolute left-16 top-1/2 transform -translate-y-1/2 z-50 bg-black text-white text-sm font-bold px-2 py-1 rounded-md opacity-0 group-hover:opacity-100 transition-opacity duration-300 tooltip-arrow pointer-events-none group-hover:pointer-events-auto"
                        style={{width: "max-content"}}
                    >Find Channels
                    </div>
                </Link>

                <Link to={"/channels/createChannel"} className={"relative group"}>
                    <div
                        className="w-12 h-12 overflow-hidden rounded-full transition-transform duration-300 ease-in-out transform hover:scale-110 hover:shadow-lg">
                        <IoIosAdd className="w-full h-full object-cover object-center bg-black"/>
                    </div>
                    <div
                        className="absolute left-16 top-1/2 transform -translate-y-1/2 z-50 bg-black text-white text-sm font-bold px-2 py-1 rounded-md opacity-0 group-hover:opacity-100 transition-opacity duration-300 tooltip-arrow pointer-events-none group-hover:pointer-events-auto"
                        style={{width: "max-content"}}
                    >
                        Create Channel
                    </div>
                </Link>

                <div className={"relative group cursor-pointer"}>
                    <div
                        className="w-12 h-12 overflow-hidden rounded-full transition-transform duration-300 ease-in-out transform hover:scale-110 hover:shadow-lg bg-black flex items-center justify-center"
                        onClick={logout}>
                        <IoLogOutOutline className="w-8 h-8" color={"white"}/>
                    </div>
                    <div
                        className="absolute left-16 top-1/2 transform -translate-y-1/2 z-50 bg-black text-white text-sm font-bold px-2 py-1 rounded-md opacity-0 group-hover:opacity-100 transition-opacity duration-300 tooltip-arrow pointer-events-none group-hover:pointer-events-auto"
                        style={{width: "max-content"}}
                    >
                        Logout
                    </div>
                </div>

                <div className="w-12 h-0.5 bg-gray-700"></div>

                <div className={"w-full scrollable-sidebar"}>
                    <InfiniteScroll
                        className="scrollable-sidebar"
                        scrollStyle="scrollable-sidebar space-y-2"
                    />
                </div>

                <div className="w-12 h-0.5 bg-gray-700"></div>

                <Link to={"/channels/about"} className={"relative group"}>
                    <div
                        className="w-10 h-10 bg-black overflow-hidden rounded-full transition-transform duration-300 ease-in-out transform hover:scale-110 hover:shadow-lg cursor-pointer flex items-center justify-center">
                        <IoInformationCircleSharp className="w-6 h-6 object-cover object-center bg-black"/>
                    </div>
                    <div
                        className="absolute left-16 top-1/2 transform -translate-y-1/2 z-50 bg-black text-white text-sm font-bold px-2 py-1 rounded-md opacity-0 group-hover:opacity-100 transition-opacity duration-300 tooltip-arrow pointer-events-none group-hover:pointer-events-auto"
                        style={{width: "max-content"}}
                    >
                        About Devs
                    </div>
                </Link>
            </div>
    );
}