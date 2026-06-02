package com.julian.miju2.presentation.transactions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.julian.miju2.domain.model.Transaction
import com.julian.miju2.domain.usecase.GetTransactionsUseCase
import com.julian.miju2.domain.usecase.GetUserNamesUseCase
import com.julian.miju2.presentation.model.TransactionUi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getUserNamesUseCase: GetUserNamesUseCase
) : ViewModel() {

    private val currencyFormat = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("es", "CO"))
    private val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("es", "CO"))

    private var userNames: Map<String, String> = emptyMap()

    var transactionsUi by mutableStateOf<List<TransactionUi>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var fullName by mutableStateOf("")
        private set

    val initial: String
        get() = fullName.trim().firstOrNull { it.isLetterOrDigit() }?.uppercaseChar()?.toString() ?: "?"

    fun loadTransactions(documentId: String) {
        if (documentId.isEmpty()) {
            transactionsUi = emptyList()
            fullName = ""
            isLoading = false
            return
        }

        isLoading = true
        getUserNamesUseCase { names ->
            userNames = names
            fullName = names[documentId] ?: ""
            getTransactionsUseCase(documentId) { lista ->
                transactionsUi = lista.map { mapToUi(it, documentId) }
                isLoading = false
            }
        }
    }

    private fun mapToUi(tx: Transaction, documentId: String): TransactionUi {
        val isIncoming = tx.to == documentId
        val otherId = if (isIncoming) tx.from else tx.to
        val counterparty = userNames[otherId] ?: otherId
        val sign = if (isIncoming) "+" else "-"
        val amountText = "$sign${currencyFormat.format(tx.amount)}"
        val subtitle = dateFormat.format(java.util.Date(tx.date))
        return TransactionUi(
            transactionId = tx.transactionId,
            counterparty = counterparty,
            subtitle = subtitle,
            amountText = amountText,
            isIncoming = isIncoming
        )
    }
}
