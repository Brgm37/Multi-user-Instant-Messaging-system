package jdbc.transactionManager.dataSource

/**
 * Represents the connection source
 */
interface ConnectionSource {
	/**
	 * Returns the connection URL
	 */
	val connectionUrl: String

	/**
	 * Returns the username
	 */
	val username: String

	/**
	 * Returns the password
	 */
	val password: String

	/**
	 * Returns the pool size
	 */
	val poolSize: Int
}
