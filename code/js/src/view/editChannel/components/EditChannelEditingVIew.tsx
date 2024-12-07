import * as React from 'react';
import {useImagePicker} from "../../components/ImagePicker/ImagePickerProvider";

const visibilityOptions = [
    "PUBLIC",
    "PRIVATE"
]

export function EditChannelEditingVIew(
    {initDescription, initVisibility, handleSubmit}: {initDescription: string, initVisibility: string, handleSubmit: (d: string, v: string, i: string) => void}
): React.JSX.Element {
    const [description, setDescription] = React.useState(initDescription)
    const [visibility, setVisibility] = React.useState(initVisibility)
    const {open, image} = useImagePicker()
    const descriptionHandler = (e: React.ChangeEvent<HTMLTextAreaElement>) => setDescription(e.target.value)
    return (
        <div className="flex items-center justify-center flex-col">
            <div>
                <div className="mb-4">
                    <label className="block text-sm font-medium mb-2" htmlFor="description">Description</label>
                    <textarea
                        id="description"
                        value={description}
                        onChange={descriptionHandler}
                        className="w-full p-2 bg-gray-700 rounded border border-gray-600 focus:outline-none focus:border-blue-500"
                        placeholder="Enter channel description"
                        rows={4}
                    ></textarea>
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
                            <option
                                key={option}
                                value={option}
                                onClick={_ => setVisibility(option)}>
                                {option}
                            </option>
                        ))}
                        value={visibility}
                    </select>
                </div>
                <div className="mb-4 justify-center flex">
                    <img src={image ? image : "/defaultIcons/default.png"} alt="icon"
                         className="w-[328px] h-[128px] object-cover rounded-lg overflow-hidden mb-2 cursor-pointer"
                         onClick={open}/>
                </div>
            </div>
            <div className="flex justify-between">
                <button
                    type={"submit"}
                    onClick={_ => handleSubmit(description, visibility, image)}
                    className="w-full p-2 bg-blue-600 rounded hover:bg-blue-700 focus:outline-none focus:bg-blue-700"
                    disabled={description === initDescription && visibility === initVisibility && image === undefined}
                >
                    Edit Channel
                </button>
            </div>
        </div>
    )
}