package jdbc.transactionManager

import Transaction
import TransactionManager
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import jakarta.inject.Named
import java.sql.SQLException
import javax.sql.DataSource

/**
 * TransactionManager implementation using JDBC
 */
@Named("TransactionManagerJDBC")
class TransactionManagerJDBC : TransactionManager {
	private val dataSource: DataSource

	init {
		val config =
			HikariConfig().apply {
				jdbcUrl = System.getenv("DB_URL")
				username = System.getenv("DB_USER")
				password = System.getenv("DB_PASSWORD")
				driverClassName = "org.postgresql.Driver"
				maximumPoolSize = System.getenv("POOL_SIZE")?.toInt() ?: 10
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
