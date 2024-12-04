import {Either, failure, success} from "../../../model/Either";

/**
 * The handler for the response from the registration service.
 *
 * @param response The response from the registration service.
 *
 * @returns The result of the response.
 */
export default async function(
    response: Response,
) :Promise<Either<AuthInfo, string>> {
    if (response.ok) {
        const data = await response.json()
        const authInfo = {
            uId: data.uId.toString(),
            expirationDate: data.expirationDate.toString(),
        }
        return success(authInfo) as Either<AuthInfo, string>
    } else {
        const error = response.text()
        return failure(error) as Either<AuthInfo, string>
    }
}