import * as React from 'react'
import {FaGithub, FaLinkedin} from "react-icons/fa";
import {Dev} from "../AboutView";

export function DevView({ dev }: { dev: Dev }): React.JSX.Element {
    return (
        <div key={dev.num} className="max-w-sm rounded overflow-hidden shadow-lg m-4 bg-gray-900">
            <img className="w-full" src={dev.imageURL} alt={`${dev.name} Image`} />
            <div className="px-6 py-4">
                <div className="font-bold text-xl mb-2">{dev.name}</div>
                <p className="text-base">{dev.num}</p>
                <p className="text-base">{dev.email}</p>
                <div className="flex space-x-2 mt-4">
                    <a href={dev.github.href} target="_blank" rel="noopener noreferrer">
                        <FaGithub size={28}/>
                    </a>
                    <a href={dev.linkedIn.href} target="_blank" rel="noopener noreferrer">
                        <FaLinkedin size={28}/>
                    </a>
                </div>
            </div>
            <div className="px-6 pb-2 w-full">
                <button
                    className="bg-black text-white font-bold py-2 px-4 rounded w-full"
                    onClick={() => window.location.href = `mailto:${dev.email}`}
                >
                    Contact
                </button>
            </div>
        </div>
    );
}