package jdbc

import org.postgresql.ds.PGSimpleDataSource
import java.sql.SQLException

abstract class JDBC(
	envName: String
) {
	private val dataSource: PGSimpleDataSource = PGSimpleDataSource()
	init {
		val connectionURL = System.getenv(envName)
		dataSource.setURL(connectionURL)
	}

	protected fun <T> connect(executor: (connection: java.sql.Connection) -> T): T {
		dataSource.connection.use { connection ->
			try {
			    connection.autoCommit = false
				val t = executor(connection)
				connection.commit()
				connection.autoCommit = true
				return t
			} catch (e: SQLException) {
				connection.rollback()
				connection.autoCommit = true
				//TODO: improve error handling
			    throw e
			}
		}
	}
}