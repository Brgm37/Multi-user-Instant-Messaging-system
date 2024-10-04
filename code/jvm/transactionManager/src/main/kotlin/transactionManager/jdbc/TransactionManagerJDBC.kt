package org.example.transactionManager.jdbc

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.example.transaction.Transaction
import org.example.transaction.jdbc.TransactionJDBC
import org.example.transactionManager.TransactionManager
import java.sql.SQLException
import javax.sql.DataSource

/**
 * TransactionManager implementation using JDBC
 * @param dbUrlEnvName Environment variable name for the database URL. Non-optional.
 * @param dbUserEnvName Environment variable name for the database user. Non-optional.
 * @param dbPasswordEnvName Environment variable name for the database password. Non-optional.
 * @param pollSizeEnvName Environment variable name for the connection pool size
 * @throws IllegalArgumentException If any of the non-optional environment variables are missing.
 */
class TransactionManagerJDBC(
	dbUrlEnvName: String,
	dbUserEnvName: String,
	dbPasswordEnvName: String,
	pollSizeEnvName: String
): TransactionManager {
	private val dataSource: DataSource

	init {
	    val dbUrl = System.getenv(dbUrlEnvName)
		val dbUser = System.getenv(dbUserEnvName)
		val dbPassword = System.getenv(dbPasswordEnvName)
		val poolSize = System.getenv(pollSizeEnvName)?.toIntOrNull()
		if (dbUrl == null || dbUser == null || dbPassword == null) {
			throw IllegalArgumentException("Missing environment variables")
		}
		val config = HikariConfig().apply {
			jdbcUrl = dbUrl
			username = dbUser
			password = dbPassword
			driverClassName = "org.postgresql.Driver"
			maximumPoolSize = poolSize ?: 10
		}
		dataSource = HikariDataSource(config)
	}

	override fun <R> run(onError: R, block: Transaction.() -> R): R {
		dataSource.connection.use { connection ->
			connection.autoCommit = false
			val transaction = TransactionJDBC(connection)
			return try {
			    val result = transaction.block()
				connection.commit()
				result
			} catch (e: SQLException) {
				transaction.rollback(onError)
			} catch (e: Exception) {
				connection.rollback()
				throw e
			}
		}
	}
}