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