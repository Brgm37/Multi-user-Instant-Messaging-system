package jdbc

import org.postgresql.ds.PGSimpleDataSource
import java.sql.SQLException

/**
 * Abstract class to connect to the database.
 *
 * This class provides a method to connect to the database and execute a lambda function.
 *
 * @param envName The name of the environment variable that contains the connection URL.
 * @property dataSource The data source to connect to the database.
 * @constructor Creates a JDBC object.
 * @see [PGSimpleDataSource] The data source to connect to the database.
 */
abstract class JDBC(
	envName: String
) {
	private val dataSource: PGSimpleDataSource = PGSimpleDataSource()
	init {
		val connectionURL = System.getenv(envName)
		dataSource.setURL(connectionURL)
	}

	/**
	 * Connects to the database and executes a lambda function.
	 *
	 * This method connects to the database, executes the lambda function,
	 * and commits the transaction if no exception is thrown.
	 * If an exception is thrown, the transaction is rolled back.
	 *
	 * @param executor The lambda function to execute.
	 * @return The result of the lambda function.
	 * @throws SQLException If an exception is thrown by the lambda function.
	 */
	protected fun <T> connect(executor: (connection: java.sql.Connection) -> T): T {
		dataSource.connection.use { connection ->
			try {
			    connection.autoCommit = false
				val t = executor(connection)
				connection.commit()
				connection.autoCommit = true
				return t
			} catch (e: SQLException) {
				//TODO: improve error handling
				connection.rollback()
				connection.autoCommit = true
			    throw e
			}
		}
	}
}