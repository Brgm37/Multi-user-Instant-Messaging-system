import React, { createContext, useContext, useState, ReactNode } from 'react';

/**
 * The context for the image picker.
 */
type ImagePickerContextType = {
    isOpen: boolean;
    image: string | undefined;
    open: () => void;
    close: () => void;
    selectImage: (image: string) => void;
    save: () => void;
};

/**
 * The image picker context.
 */
const ImagePickerContext = createContext<ImagePickerContextType | undefined>(undefined);

/**
 * The image picker provider.
 *
 * @param children the children
 */
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

/**
 * The use image picker.
 */
export const useImagePicker = () => {
    const context = useContext(ImagePickerContext);
    if (!context) {
        throw new Error('useImagePicker must be used within an ImagePickerProvider');
    }
    return context;
};