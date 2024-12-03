import { v4 as uuidv4 } from 'uuid';
import React, {ReactNode} from "react";
import useSignal from "../../utils/useSignal";
import {Either, failure, success} from "../../../model/Either";
import { CreateChannelInvitationMockServiceContext } from './CreateChannelInvitationMockServiceContext';
import {AccessControl} from "../../../view/createChannelInvitation/model/createChannelInvitation";

export function CreateChannelInvitationMockServiceProvider(props: { children: ReactNode }): React.JSX.Element {
    const signal = useSignal()
    const service = {
        async createChannelInvitation(
            expirationDate: string,
            maxUses: number,
            accessControl: AccessControl
        ): Promise<Either<{invitationCode: string}, string>> {
            return new Promise((resolve) => {
                setTimeout(() => {
                    resolve(success({invitationCode: uuidv4()}) as Either<{ invitationCode: string }, string>)
                }, 1000);
            });
        }
    }
    return (
        <CreateChannelInvitationMockServiceContext.Provider value={service}>
            {props.children}
        </CreateChannelInvitationMockServiceContext.Provider>
    )
}