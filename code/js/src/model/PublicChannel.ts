
/**
 * @description Type for the state of the PublicChannels form.
 * 
 * @type PublicChannel
 * @prop id The id of the public channel.
 * @prop name The name of the public channel.
 */
export type PublicChannel = {
    id: number,
    name: string,
    owner: string,
    description: string,
    icon: string,
}

