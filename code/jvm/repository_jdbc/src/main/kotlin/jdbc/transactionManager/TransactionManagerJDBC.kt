package jdbc.transactionManager

import Transaction
import TransactionManager
import utils.encryption.Encrypt
import java.sql.SQLException
import javax.sql.DataSource

/**
 * TransactionManager implementation using JDBC
 *
 * @property dataSource the data source to connect to the database
 * @property encrypt the encryption utility to use
 */
class TransactionManagerJDBC(
    private val dataSource: DataSource,
    private val encrypt: Encrypt,
) : TransactionManager {
    override fun <R> run(block: Transaction.() -> R): R {
        dataSource.connection.use { connection ->
            connection.autoCommit = false
            val transaction = TransactionJDBC(connection, encrypt)
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