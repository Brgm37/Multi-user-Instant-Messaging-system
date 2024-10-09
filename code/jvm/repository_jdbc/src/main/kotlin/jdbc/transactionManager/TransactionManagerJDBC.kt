package jdbc.transactionManager

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import jakarta.inject.Named
import Transaction
import TransactionManager
import jakarta.inject.Inject
import jdbc.transactionManager.dataSource.ConnectionSource
import java.sql.SQLException
import javax.sql.DataSource

/**
 * TransactionManager implementation using JDBC
 */
@Named("TransactionManagerJDBC")
class TransactionManagerJDBC @Inject constructor(
	@Named("PostgresSQLConnectionSource") private val dS: ConnectionSource
): TransactionManager {
	private val dataSource: DataSource
	init {
		val config = HikariConfig().apply {
			jdbcUrl = dS.connectionUrl
			username = dS.username
			password = dS.password
			driverClassName = "org.postgresql.Driver"
			maximumPoolSize = dS.poolSize
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