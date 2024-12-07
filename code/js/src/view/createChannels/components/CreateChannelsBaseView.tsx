import {ChannelInput, CreateChannelsState, Visibility} from "../hooks/states/createChannelsState";
import React from "react";
import {AccessControl} from "../../../model/AccessControl";
import {InputBar} from "../../components/InputBar";
import {UseCreateChannelHandler} from "../hooks/handler/UseCreateChannelHandler";
import {useImagePicker} from "../../components/ImagePicker/ImagePickerProvider";

const accessControlOptions = [
    "READ_ONLY",
    "READ_WRITE"
]

const visibilityOptions = [
    "PUBLIC",
    "PRIVATE"
]

export function CreateChannelsBaseView(
    {handler, state}: {
        handler: UseCreateChannelHandler,
        state: CreateChannelsState,
    }
): React.JSX.Element {
    const { open, image } = useImagePicker();

    const handleSubmit = (event: React.FormEvent) => {
        event.preventDefault();
        const visibility = (document.querySelector("select[title=visibility]") as HTMLSelectElement).value as Visibility;
        const accessControl = (document.querySelector("select[title=accessControl]") as HTMLSelectElement).value as AccessControl;
        const channel: ChannelInput = {
            name: state.input.name,
            visibility: visibility,
            access: accessControl,
            isValid: state.input.isValid,
            description: state.input.description,
            icon: image
        };
        handler.onSubmit(channel);
    }
    return (
        <form onSubmit={handleSubmit} className="bg-gray-900 p-6 rounded-lg shadow-lg">
            <div className="mb-4">
                <label className="block text-sm font-medium mb-2" htmlFor="channelName">Channel Name</label>
                <InputBar
                    value={state.input.name}
                    onChange={handler.onNameChange}
                    placeholder={"Channel Name"}
                    className={"w-full p-2 bg-gray-700 rounded border border-gray-600 focus:outline-none focus:border-blue-500"}/>
                {!state.input.isValid && state.input.name.length > 0 &&
                    <span className="text-sm text-red-400">
                        You can´t have two channels with the same name.
                    </span>
                }
            </div>

            <div className="mb-4">
                <label className="block text-sm font-medium mb-1" htmlFor="visibility">
                    <br/>
                    Choose Channel Visibility
                </label>
                <select
                    id="visibility"
                    title={"visibility"}
                    className="w-full p-2 bg-gray-700 rounded border border-gray-600 focus:outline-none focus:border-blue-500">
                    {visibilityOptions.map((option) => (
                        <option key={option} value={option}>{option}</option>
                    ))}
                </select>
            </div>

            <div className="mb-4">
                <label className="block text-sm font-medium mb-2" htmlFor="accessControl">Access Control</label>
                <select id={"accessControl"} title={"accessControl"}
                        className="w-full p-2 bg-gray-700 rounded border border-gray-600 focus:outline-none focus:border-blue-500">
                    {accessControlOptions.map((option) => (<option key={option} value={option}>{option}</option>))}
                </select>
                <span className="text-sm text-gray-400">
                    Warning: Notice that for private channels it will only serve as a default option.
                </span>
            </div>

            <div className="mb-4">
                <label className="block text-sm font-medium mb-2" htmlFor="description">Description</label>
                <textarea
                    id="description"
                    value={state.input.description}
                    onChange={(e) => handler.onDescriptionChange(e.target.value)}
                    className="w-full p-2 bg-gray-700 rounded border border-gray-600 focus:outline-none focus:border-blue-500"
                    placeholder="Enter channel description"
                    rows={4}
                ></textarea>
            </div>

            <label className="block text-sm font-medium mb-2" htmlFor="icon">Channel Icon</label>

            <div className="mb-4 justify-center flex">
                <img src={image ? image : "/defaultIcons/default.png"} alt="icon"
                     className="w-[328px] h-[128px] object-cover rounded-lg overflow-hidden mb-2 cursor-pointer" onClick={open}/>
            </div>

            <div className="flex justify-between">
                <button
                    type={"submit"}
                    onClick={handleSubmit}
                    className="w-full p-2 bg-blue-600 rounded hover:bg-blue-700 focus:outline-none focus:bg-blue-700"
                    disabled={!state.input.isValid}
                >
                    Create Channel
                </button>
            </div>
        </form>
    )
}