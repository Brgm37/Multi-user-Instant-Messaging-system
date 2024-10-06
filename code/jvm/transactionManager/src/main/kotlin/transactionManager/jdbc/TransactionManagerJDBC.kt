package transactionManager.jdbc

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import jakarta.inject.Inject
import jakarta.inject.Named
import org.example.transaction.Transaction
import org.example.transaction.jdbc.TransactionJDBC
import transactionManager.TransactionManager
import java.sql.SQLException
import javax.sql.DataSource

/**
 * TransactionManager implementation using JDBC
 * @param dbUrl The connection URL for the database.
 * @param dbUser The username for the database.
 * @param dbPassword The password for the database.
 * @param poolSize The maximum number of connections to keep in the pool.
 */
@Named("TransactionManagerJDBC")
class TransactionManagerJDBC @Inject constructor(
	@Named("DB_URL") dbUrl: String,
	@Named("DB_USER") dbUser: String,
	@Named("DB_PASSWORD") dbPassword: String,
	@Named("POOL_SIZE") poolSize: Int? = null
): TransactionManager {
	private val dataSource: DataSource

	init {
		val config = HikariConfig().apply {
			jdbcUrl = dbUrl
			username = dbUser
			password = dbPassword
			driverClassName = "org.postgresql.Driver"
			maximumPoolSize = poolSize ?: 10
		}
		dataSource = HikariDataSource(config)
	}

	override fun <R> run(block: Transaction.() -> R): R {
		dataSource.connection.use { connection ->
			connection.autoCommit = false
			val transaction = TransactionJDBC(connection)
			return try {
			    val result = transaction.block()
				connection.commit()
				result
			} catch (e: SQLException) {
				transaction.rollback()
				throw e
			}
		}
	}
}