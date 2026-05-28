package com.julian.miju2.DashboardModule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase

class DashboardViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().getReference("users")
    private val accountsRef = FirebaseDatabase.getInstance().getReference("accounts")
    private val transactionsRef = FirebaseDatabase.getInstance().getReference("transactions")

    private val currencyFormat = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("es", "CO"))
    private val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("es", "CO"))

    val initial: String
        get() = fullName.trim()
            .firstOrNull { it.isLetterOrDigit() }
            ?.uppercaseChar()
            ?.toString()
            ?: "?"
    var fullName by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    // Saldo leído desde el nodo 'accounts' en Firebase
    var balance by mutableStateOf(0.0)
        private set

    var accountNumber by mutableStateOf("")
        private set

    var transactions by mutableStateOf<List<Transaction>>(emptyList())
        private set

    var transactionsUi by mutableStateOf<List<TransactionUi>>(emptyList())
        private set

    private var userNames: Map<String, String> = emptyMap()

    val formattedBalance: String
        get() = currencyFormat.format(balance)

    fun loadUserData(documentId: String) {
        if (documentId.isEmpty()) {
            fullName = ""
            balance = 0.0
            accountNumber = ""
            isLoading = false
            return
        }

        isLoading = true
        database.child(documentId).get().addOnSuccessListener { snapshot ->
            fullName = if (snapshot.exists()) snapshot.child("fullName").value?.toString() ?: "" else ""
            isLoading = false
        }.addOnFailureListener {
            fullName = ""
            isLoading = false
        }

        accountsRef.child(documentId).get().addOnSuccessListener { snapshot ->
            balance = snapshot.child("balance").getValue(Double::class.java) ?: 0.0
            accountNumber = snapshot.child("accountNumber").value?.toString() ?: ""
        }.addOnFailureListener {
            balance = 0.0
            accountNumber = ""
        }
    }

    fun loadTransactions(documentId: String) {
        if (documentId.isEmpty()) {
            transactions = emptyList()
            transactionsUi = emptyList()
            return
        }

        loadUserNames {
            transactionsRef.get().addOnSuccessListener { snapshot ->
                val lista = snapshot.children
                    .mapNotNull { child ->
                        child.getValue(Transaction::class.java)?.copy(transactionId = child.key ?: "")
                    }
                    .filter { it.from == documentId || it.to == documentId }
                    .sortedByDescending { it.date }
                transactions = lista
                transactionsUi = lista.map { mapToUi(it, documentId) }
            }.addOnFailureListener {
                transactions = emptyList()
                transactionsUi = emptyList()
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
