package com.julian.miju2.domain.repository

import com.julian.miju2.domain.model.Transaction

interface TransactionRepository {
    fun getAllTransactions(onResult: (List<Transaction>) -> Unit)

    fun createTransaction(tx: Map<String, Any?>, onResult: (Boolean) -> Unit)
}
