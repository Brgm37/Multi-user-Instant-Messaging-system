/**
 * Interface for the FindChannels Mock service.
 *
 * @method getChannelsByPartialName
 * @method joinChannel
 */
export type FindChannelsMockService = {

        getChannelsByPartialName(
            partialName: string,
            onSuccess: (response: Response) => void,
            onError: (error: Error) => void
        ): void

        joinChannel(
            channelId: number,
            onSuccess: (response: Response) => void,
            onError: (error: Error) => void
        ): void

        getPublicChannels(
            offset: number,
            limit: number,
            onSuccess: (response: Response) => void,
            onError: (error: Error) => void
        ): void
}

export function makeDefaultFindChannelsMockService() : FindChannelsMockService {
    return {
        getChannelsByPartialName: (partialName, onSuccess, onError) => {
            if (partialName.includes("1")) {
                let response =
                    new Response(
                        JSON.stringify(
                            [
                                {
                                    name: {name: "test1", displayName: "test1"},
                                    id: 1,
                                    owner: {id: 1, username: "test1"},
                                },
                            ]
                        )
                    )
                onSuccess(response)
            } else onError(Error("Something went wrong"))
        },
        joinChannel: (channelId, onSuccess, onError) => {
            if (channelId === 1) {
                let response = new Response(JSON.stringify({}))
                onSuccess(response)
            } else onError(Error("Something went wrong"))
        },
        getPublicChannels: (offset, limit, onSuccess, onError) => {
            return onSuccess(new Response(
                JSON.stringify(
                    [
                        {
                            name: {name: "test", displayName: "test1"},
                            id: 1,
                            owner: {id: 1, username: "test1"},
                        },
                        {
                            name: {name: "test", displayName: "test2"},
                            id: 2,
                            owner: {id: 2, username: "test2"},
                        },
                        {
                            name: {name: "test", displayName: "test3"},
                            id: 3,
                            owner: {id: 3, username: "test3"},
                        },
                    ]
                )
            ))
        }
    }
}
