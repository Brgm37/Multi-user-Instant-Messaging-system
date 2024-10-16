package jdbc.transactionManager

import Transaction
import TransactionManager
import java.sql.SQLException
import javax.sql.DataSource

/**
 * TransactionManager implementation using JDBC
 */
class TransactionManagerJDBC(
    private val dataSource: DataSource,
) : TransactionManager {
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