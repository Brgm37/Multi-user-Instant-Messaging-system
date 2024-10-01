package utils

/**
 * Represents the result of an operation.
 * @param L The type of the error.
 * @param R The type of the success.
 */
sealed class Either<out L, out R> {
	data class Left<out L>(val value: L) : Either<L, Nothing>()

	data class Right<out R>(val value: R) : Either<Nothing, R>()
}

/**
 * Represents the success of an operation.
 * @param value The value of the success.
 */
fun <R> success(value: R) = Either.Right(value)

/**
 * Represents the failure of an operation.
 * @param error The error of the failure.
 */
fun <L> failure(error: L) = Either.Left(error)

typealias Success<S> = Either.Right<S>
typealias Failure<F> = Either.Left<F>