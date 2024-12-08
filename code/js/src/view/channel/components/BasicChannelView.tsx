import * as React from "react";
import {Outlet, useNavigate, useParams} from "react-router-dom";
import MessageInfiniteScroll from "./messageInfiniteScroll/ChannelMessageInfiniteScroll";
import {Channel} from "../../../model/Channel";
import {useContext, useEffect} from "react";
import {ChannelServiceContext} from "../../../service/channel/ChannelServiceContext";
import {AuthUserContext} from "../../session/AuthUserContext";
import {MdDelete, MdEdit} from "react-icons/md";
import {FaPersonRunning} from "react-icons/fa6";
import {IoMdPersonAdd} from "react-icons/io";
import {IoSend} from "react-icons/io5";

export default function BasicChannelView(
    {error, errorDismiss, onSend, onError}: { error?: string, errorDismiss?: () => void, onSend(msg: string): void, onError(err: string): void }
): React.JSX.Element {
    const [message, setMessage] = React.useState<string>("");
    const {id} = useParams()
    const [channel, setChannel] = React.useState<Channel>()
    const {loadChannel, leaveOrDelete} = useContext(ChannelServiceContext)
    const userContext = useContext(AuthUserContext)
    const navigation = useNavigate()

    useEffect(() => {
        loadChannel(id).then(response => {
            if (response.tag === "success") setChannel(response.value)
            else navigation("/channels")
        })
    }, [id])

    const handleInput = (event: React.ChangeEvent<HTMLInputElement>) => setMessage(event.target.value)

    const sendMessage = () => {
        if (message === "") return
        onSend(message)
        setMessage("")
    }

    const handleKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
        if (event.key === "Enter") sendMessage()
    }

    const handleSend = () => sendMessage()

    const handleInvite = () => navigation("/channels/" + id + "/createInvitation");

    const handleEdit = () => navigation("/channels/" + id + "/edit");

    const handleLeaveOrDelete = () => {
        leaveOrDelete(id).then(response => {
            if (response.tag === "success") {
                navigation("/channels")
                window.location.reload()
            }
            else onError(response.value)
        })
    }

    const isOwner = channel !== undefined && channel.owner.id === Number(userContext.id)

    const isChannelPrivate = channel !== undefined && channel.visibility === "PRIVATE"

    const isAccessControlReadWrite = channel !== undefined && channel.accessControl === "READ_WRITE"

    let channelIcon = ""
    let channelName = ""

    if (channel !== undefined) channelIcon = channel.icon
    if (channel !== undefined) channelName = channel.name

    return (
        <div className="relative h-screen">
            <div className={"flex flex-col h-full"}>
                <header className="bg-gray-900 p-4 flex items-center w-full">
                    <div
                        className="w-14 h-14 overflow-hidden rounded-full">
                        <img src={channelIcon} alt={channelName} className="w-full h-full object-cover object-center"/>
                    </div>
                    <h2 className="text-xl font-bold text-white ml-4">{channelName}</h2>
                    <div className="ml-auto flex items-center space-x-4">
                        {isOwner && (
                            <div className={"relative group"}>
                                <div
                                    className="w-12 h-12 bg-black overflow-hidden rounded-full transition-transform duration-300 ease-in-out transform hover:scale-110 hover:shadow-lg cursor-pointer flex items-center justify-center"
                                    onClick={handleEdit}
                                >
                                    <MdEdit className="w-6 h-6"/>
                                </div>
                                <div
                                    className="absolute left-1/2 top-full transform -translate-x-1/2 mt-2 z-50 bg-black text-white text-sm font-bold px-2 py-1 rounded-md opacity-0 group-hover:opacity-100 transition-opacity duration-300 tooltip-arrow pointer-events-none group-hover:pointer-events-auto"
                                    style={{width: "max-content"}}
                                >
                                    Edit
                                </div>
                            </div>
                        )}
                        {isOwner && isChannelPrivate && (
                            <div className={"relative group"}>
                                <div
                                    className="w-12 h-12 bg-black overflow-hidden rounded-full transition-transform duration-300 ease-in-out transform hover:scale-110 hover:shadow-lg cursor-pointer flex items-center justify-center"
                                    onClick={handleInvite}
                                >
                                    <IoMdPersonAdd className="w-6 h-6"/>
                                </div>
                                <div
                                    className="absolute left-1/2 top-full transform -translate-x-1/2 mt-2 z-50 bg-black text-white text-sm font-bold px-2 py-1 rounded-md opacity-0 group-hover:opacity-100 transition-opacity duration-300 tooltip-arrow pointer-events-none group-hover:pointer-events-auto"
                                    style={{width: "max-content"}}
                                >
                                    Invite
                                </div>
                            </div>
                        )}
                        <div className="relative group">
                            <div
                                className="w-12 h-12 bg-black overflow-hidden rounded-full transition-transform duration-300 ease-in-out transform hover:scale-110 hover:shadow-lg cursor-pointer flex items-center justify-center"
                                onClick={handleLeaveOrDelete}
                            >
                                {isOwner ? (
                                    <MdDelete className="w-6 h-6 text-red-800"/>
                                ) : (
                                    <FaPersonRunning className="w-6 h-6 text-red-800"/>
                                )
                                }
                            </div>
                            <div
                                className="absolute left-1/2 top-full transform -translate-x-1/2 mt-2 z-50 bg-black text-white text-sm font-bold px-2 py-1 rounded-md opacity-0 group-hover:opacity-100 transition-opacity duration-300 tooltip-arrow pointer-events-none group-hover:pointer-events-auto"
                                style={{width: "max-content"}}
                            >
                                {isOwner ? "Delete" : "Leave"}
                            </div>
                        </div>
                    </div>
                </header>

                <MessageInfiniteScroll
                    className={"flex-1 bg-gray-800 p-4 overflow-y-auto flex-col-reverse custom-scrollbar"}
                    scrollStyle={"flex-1 bg-gray-800  overflow-y-auto flex flex-col-reverse"}
                />
                {error && (
                    <div className="bg-red-500 text-white p-2 rounded">
                        {error}
                        <button
                            className="ml-2"
                            onClick={errorDismiss}
                        >X
                        </button>
                    </div>
                )}
                <footer className={"flex items-center p-3"}>
                    {isAccessControlReadWrite && (
                        <>
                            <input
                                className={"flex-1 p-2 bg-gray-900 text-gray-200 border border-gray-700 mr-1"}
                                placeholder={"Type a message"}
                                value={message}
                                onChange={handleInput}
                                onKeyDown={handleKeyDown}/>

                            <div
                                className="w-10 h-10 bg-gray-900 overflow-hidden rounded-lg cursor-pointer flex items-center justify-center"
                                onClick={handleSend}
                            >
                                <IoSend className="w-6 h-6"/>
                            </div>
                        </>
                    )}
                </footer>
            </div>
            <div className="absolute bottom-0 w-full z-50">
                <Outlet/>
            </div>
        </div>
    )
        ;
}