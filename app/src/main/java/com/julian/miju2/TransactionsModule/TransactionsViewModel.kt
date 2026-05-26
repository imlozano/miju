package com.julian.miju2.TransactionsModule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.julian.miju2.DashboardModule.Transaction
import com.julian.miju2.DashboardModule.TransactionUi

class TransactionsViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().getReference("users")
    private val transactionsRef = FirebaseDatabase.getInstance().getReference("transactions")

    private val currencyFormat = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("es", "CO"))
    private val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("es", "CO"))

    private var userNames: Map<String, String> = emptyMap()

    var transactionsUi by mutableStateOf<List<TransactionUi>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun loadTransactions(documentId: String) {
        if (documentId.isEmpty()) {
            transactionsUi = emptyList()
            isLoading = false
            return
        }

        isLoading = true
        loadUserNames {
            transactionsRef.get().addOnSuccessListener { snapshot ->
                val lista = snapshot.children
                    .mapNotNull { child ->
                        child.getValue(Transaction::class.java)?.copy(transactionId = child.key ?: "")
                    }
                    .filter { it.from == documentId || it.to == documentId }
                    .sortedByDescending { it.date }
                transactionsUi = lista.map { mapToUi(it, documentId) }
                isLoading = false
            }.addOnFailureListener {
                transactionsUi = emptyList()
                isLoading = false
            }
        }
    }

    private fun loadUserNames(onLoaded: () -> Unit) {
        database.get().addOnSuccessListener { snapshot ->
            userNames = snapshot.children.associate { child ->
                (child.key ?: "") to (child.child("fullName").value?.toString() ?: "")
            }
            onLoaded()
        }.addOnFailureListener {
            userNames = emptyMap()
            onLoaded()
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
