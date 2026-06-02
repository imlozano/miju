package com.julian.miju2.domain.usecase

import com.julian.miju2.domain.model.Transaction
import com.julian.miju2.domain.repository.TransactionRepository
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(private val repository: TransactionRepository) {
    operator fun invoke(documentId: String, onResult: (List<Transaction>) -> Unit) {
        repository.getAllTransactions { transactions ->
            val filtered = transactions
                .filter { it.from == documentId || it.to == documentId }
                .sortedByDescending { it.date }
            onResult(filtered)
        }
    }
}
