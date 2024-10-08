package transactionManager

import transaction.Transaction

interface TransactionManager {
	fun <R> run(block: Transaction.() -> R): R
}