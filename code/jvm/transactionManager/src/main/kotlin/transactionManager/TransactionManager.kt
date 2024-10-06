package transactionManager

import org.example.transaction.Transaction

interface TransactionManager {
	fun <R> run(block: Transaction.() -> R): R
}