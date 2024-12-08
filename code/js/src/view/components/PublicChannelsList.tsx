import React from 'react';
import {PublicChannel} from "../../model/PublicChannel";

/**
 * The props for the public channels list.
 */
type PublicChannelsListProps = {
    channels: PublicChannel[];
    onClick : (channelId: number) => void;
}

/**
 * The public channels list.
 *
 * @param channels
 * @param onClick
 * @constructor
 */
export function PublicChannelsList(
    {channels, onClick}: PublicChannelsListProps
): React.JSX.Element  {
    return (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
            {channels.map((channel, index) => (
                <div key={index} className="bg-gray-900 rounded-lg overflow-hidden card">
                    <img alt={`Server ${channel.name}`} className="w-full h-40 object-cover" src={channel.icon}/>
                        <div className="p-4">
                            <div className="flex items-center space-x-2 mb-2">
                                <img src={channel.icon} className="h-10 w-10 rounded-full object-cover object-center" alt={channel.name}/>
                                <h3 className="text-lg font-bold text-white">{channel.name}</h3>
                            </div>
                            <p className="text-gray-400 mb-4">{channel.description}</p>
                            <div className="flex justify-between text-sm text-gray-400">
                                <span className={"font-bold text-white"}>{channel.owner}</span>
                            </div>
                            <button className="bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-700 w-full mt-4"
                                    onClick={() => onClick(channel.id)}>Join
                            </button>
                        </div>
                </div>
                ))}
        </div>
    );
}