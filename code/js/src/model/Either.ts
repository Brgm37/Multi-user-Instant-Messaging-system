/**
 * Either type
 *
 * @param L the left type
 * @param R the right type
 */
export type Either<L, R> = { tag: "success", value: L } | { tag: "failure", value: R }

/**
 * Create a new Either with a left value
 *
 * @param value
 */
export function success<L, R>(value: L): Either<L, R> {
    return { tag: "success", value }
}

/**
 * Create a new Either with a right value
 *
 * @param value
 */
export function failure<L, R>(value: R): Either<L, R> {
    return { tag: "failure", value }
}

/**
 * Check if the Either is a success
 *
 * @param either
 */
export function isSuccess<L, R>(either: Either<L, R>): either is { tag: "success", value: L } {
    return either.tag === "success"
}

/**
 * Check if the Either is a failure
 *
 * @param either
 */
export function isFailure<L, R>(either: Either<L, R>): either is { tag: "failure", value: R } {
    return either.tag === "failure"
}