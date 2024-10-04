package org.example.transactionManager

import org.example.transaction.Transaction

interface TransactionManager {
	fun <R> run(onError: R, block: Transaction.() -> R): R
}