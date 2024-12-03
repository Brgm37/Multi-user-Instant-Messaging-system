import React, { createContext, useContext, useState, ReactNode } from 'react';

type ImagePickerContextType = {
    isOpen: boolean;
    image: string | undefined;
    open: () => void;
    close: () => void;
    selectImage: (image: string) => void;
    save: () => void;
};

const ImagePickerContext = createContext<ImagePickerContextType | undefined>(undefined);

export const ImagePickerProvider = ({ children }: { children: ReactNode }) => {
    const [isOpen, setIsOpen] = useState(false);
    const [image, setImage] = useState<string | undefined>(undefined);

    const open = () => setIsOpen(true);
    const close = () => {
        setImage(undefined);
        setIsOpen(false);
    }

    const save = () => {
        setIsOpen(false);
    }

    const selectImage = (image: string) => {
        setImage(image);
    }

    return (
        <ImagePickerContext.Provider value={{ isOpen, open, close, selectImage, image, save }}>
            {children}
        </ImagePickerContext.Provider>
    );
};

export const useImagePicker = () => {
    const context = useContext(ImagePickerContext);
    if (!context) {
        throw new Error('useImagePicker must be used within an ImagePickerProvider');
    }
    return context;
};