import React, { useState } from 'react';
import {useImagePicker} from "./ImagePickerProvider";

type Image = {
    id: number;
    src: string;
}

const imagesList: Image[] = [
    { id: 1, src: '/defaultIcons/icon1.jpg' },
    { id: 2, src: '/defaultIcons/icon2.jpg' },
    { id: 3, src: '/defaultIcons/icon3.jpg' },
    { id: 4, src: '/defaultIcons/icon4.png' },
    { id: 5, src: '/defaultIcons/icon5.png' },
    { id: 6, src: '/defaultIcons/icon6.png' },
    { id: 7, src: '/defaultIcons/icon7.png' },
    { id: 8, src: '/defaultIcons/icon8.png' },
    { id: 9, src: '/defaultIcons/icon9.png' },
    { id: 10, src: '/defaultIcons/icon10.png' },
    { id: 11, src: '/defaultIcons/icon11.png' },
    { id: 12, src: '/defaultIcons/icon12.png' },
    { id: 13, src: '/defaultIcons/icon13.png' },
    { id: 14, src: '/defaultIcons/icon14.png' },
    { id: 15, src: '/defaultIcons/icon15.png' },
    { id: 16, src: '/defaultIcons/icon16.png' },
    { id: 17, src: '/defaultIcons/icon17.png' },
    { id: 18, src: '/defaultIcons/icon18.png' },
    { id: 19, src: '/defaultIcons/icon19.png' },
    { id: 20, src: '/defaultIcons/icon20.png' },
];

const ImagePicker = () => {
    const { isOpen, close, image, selectImage, save } = useImagePicker();
    const [selectedImage, setSelectedImage] = useState<string | null>(null);

    const handleImageClick = (src: string) => {
        setSelectedImage(src);
        selectImage(src);
    };

    if (!isOpen) return null;

    return (
        <div className="absolute inset-0 z-50 flex items-center justify-center">
            <div className="w-96 max-w-md mx-auto bg-gray-800 rounded-lg shadow-lg overflow-hidden">
                <div className="px-6 py-4 bg-gray-800 flex justify-between items-center">
                    <h2 className="text-white text-lg font-semibold">CHOOSE AN ICON</h2>
                    <button onClick={close} className="text-gray-400 hover:text-white">
                        <i className="fas fa-times"></i>
                    </button>
                </div>
                <div className="px-6 py-4 space-y-4 h-96 overflow-y-scroll custom-scrollbar" style={{height: '590px'}}>
                    {imagesList.map((item) => (
                        <div
                            key={item.id}
                            onClick={() => handleImageClick(item.src)}
                            className="bg-gray-600 rounded-lg overflow-hidden mb-2"
                            style={{
                                cursor: 'pointer',
                                border: selectedImage === item.src ? '2px solid blue' : 'none',
                            }}
                        >
                            <img src={item.src} alt={`Image ${item.id}`} className="w-full h-32 object-cover"/>
                        </div>
                    ))}
                </div>
                <button
                    className="bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-700 w-full mt-4"
                    onClick={save}
                    disabled={!selectedImage}
                >Save
                </button>
            </div>
        </div>
    );
};

export default ImagePicker;