package jdbc.transactionManager

import Transaction
import TransactionManager
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import jdbc.transactionManager.dataSource.ConnectionSource
import java.sql.SQLException
import javax.sql.DataSource

/**
 * TransactionManager implementation using JDBC
 */
class TransactionManagerJDBC(
    private val dS: ConnectionSource,
) : TransactionManager {
    private val dataSource: DataSource

    init {
        val config =
            HikariConfig()
                .apply {
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
            } finally {
                connection.autoCommit = true
            }
        }
    }
}