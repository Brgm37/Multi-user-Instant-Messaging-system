import * as React from "react";
import MessageInfiniteScroll from "./messageInfiniteScroll/ChannelMessageInfiniteScroll";
import {useNavigate, useParams} from "react-router-dom";
import {Channel} from "../../../model/Channel";
import {useContext, useEffect} from "react";
import {ChannelServiceContext} from "../../../service/channel/ChannelServiceContext";

export default function BasicChannelView(
    {error, errorDismiss, onSend}: { error?: string, errorDismiss?: () => void, onSend(msg: string): void}
): React.JSX.Element {
    const [message, setMessage] = React.useState<string>("");
    const {id} = useParams()
    const [channel, setChannel] = React.useState<Channel>()
    const {loadChannel} = useContext(ChannelServiceContext)
    const navigation = useNavigate()

    useEffect(() => {
        loadChannel(id).then(response => {
            if (response.tag === "success") setChannel(response.value)
            else navigation("/channels")
        })
    }, [id])

    const handleInput = (event: React.ChangeEvent<HTMLInputElement>) => setMessage(event.target.value)

    const sendMessage = () => {
        onSend(message)
        setMessage("")
    }

    const handleKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
        if (event.key === "Enter") sendMessage()
    }

    const handleSend = () => sendMessage()

    return (
        <div className={"flex flex-col h-screen"}>
            <MessageInfiniteScroll
                className={"flex-1 bg-gray-800 p-4 overflow-y-auto flex-col-reverse"}
                scrollStyle={"flex-1 bg-gray-800 p-4 overflow-y-auto flex flex-col-reverse"}
            />
            {error && (
                <div className="bg-red-500 text-white p-2 rounded">
                    {error}
                    <button
                        className="ml-2"
                        onClick={errorDismiss}
                    >X</button>
                </div>
            )}
            <footer className={"flex items-center p-3"}>
                <input
                    className={"flex-1 p-2 bg-gray-900 text-gray-200 border border-gray-700"}
                    placeholder={"Type a message"}
                    value={message}
                    onChange={handleInput}
                    onKeyDown={handleKeyDown}
                />
                <button
                    className={"rounded ml-auto hover:bg-gray-800 p-2"}
                    onClick={handleSend}
                >
                    Send
                </button>
            </footer>
        </div>
    );
}