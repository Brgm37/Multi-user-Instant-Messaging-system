import * as React from "react";
import InfiniteScroll from "../../../components/infiniteScroll/InfiniteScroll";
import {useContext, useEffect} from "react";
import {ChannelsServiceContext} from "../../../../service/channels/ChannelsServiceContext";
import {Channel} from "../../../../model/Channel";
import {useNavigate} from "react-router-dom";

const TIMEOUT = 500;

export function Sidebar(): React.JSX.Element {
    const [channels, setChannels] = React.useState<Channel[]>([])
    const [search, setSearch] = React.useState<string>("")
    const {findChannelsByName} = useContext(ChannelsServiceContext)
    const onSearchChange = (value: React.ChangeEvent<HTMLInputElement>) => setSearch(value.target.value)
    const navigate = useNavigate()
    useEffect(() => {
        const timeout = setTimeout(() => {
            findChannelsByName(search, 0, 15)
                .then(response => {if (response.tag === "success") setChannels(response.value)})
        }, TIMEOUT);
        return () => clearTimeout(timeout);
    }, [search]);
    useEffect(() => {
        const timeout = setTimeout(() => {
            const c = channels.find(channel => channel.name === search)
            if (c) navigate(`/channels/${c.id}`)
        }, TIMEOUT);
        return () => clearTimeout(timeout);
    }, [search]);
    return (
        <div className="w-64 bg-gray-900 text-white flex flex-col">
            <div className={"bg-gray-700 text-white p-2 rounded ml-auto w-full"}>
                <input
                    type="text"
                    value={search}
                    onChange={onSearchChange}
                    placeholder="Search for channels"
                    className="bg-gray-700 text-white text-center w-full"
                    list="sugesstions"
                />
                <datalist
                    id="sugesstions"
                    className={"bg-pink-700 text-white p-2 rounded ml-auto w-full"}
                >
                    {channels.map(channel => (<option key={channel.id} value={channel.name}/>))}
                </datalist>
            </div>
            <InfiniteScroll
                className="flex-1 overflow-y-auto"
                scrollStyle="custom-scrollbar"
            />
        </div>
    );
}