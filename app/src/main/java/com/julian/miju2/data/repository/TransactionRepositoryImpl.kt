package com.julian.miju2.data.repository

import com.julian.miju2.data.datasource.FirebaseTransactionDataSource
import com.julian.miju2.domain.model.Transaction
import com.julian.miju2.domain.repository.TransactionRepository

class TransactionRepositoryImpl(
    private val dataSource: FirebaseTransactionDataSource = FirebaseTransactionDataSource()
) : TransactionRepository {

    override fun getAllTransactions(onResult: (List<Transaction>) -> Unit) {
        dataSource.getAllTransactions()
            .addOnSuccessListener { snapshot ->
                val transactions = snapshot.children.mapNotNull { child ->
                    child.getValue(Transaction::class.java)?.copy(transactionId = child.key ?: "")
                }
                onResult(transactions)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    override fun createTransaction(tx: Map<String, Any?>, onResult: (Boolean) -> Unit) {
        dataSource.createTransaction(tx)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }
}
