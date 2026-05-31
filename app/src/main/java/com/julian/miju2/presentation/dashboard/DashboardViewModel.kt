package com.julian.miju2.presentation.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.julian.miju2.data.repository.AccountRepositoryImpl
import com.julian.miju2.data.repository.TransactionRepositoryImpl
import com.julian.miju2.data.repository.UserRepositoryImpl
import com.julian.miju2.domain.model.Transaction
import com.julian.miju2.domain.usecase.GetAccountUseCase
import com.julian.miju2.domain.usecase.GetTransactionsUseCase
import com.julian.miju2.domain.usecase.GetUserDataUseCase
import com.julian.miju2.domain.usecase.GetUserNamesUseCase
import com.julian.miju2.presentation.model.TransactionUi

class DashboardViewModel : ViewModel() {

    private val userRepository = UserRepositoryImpl()
    private val accountRepository = AccountRepositoryImpl()
    private val transactionRepository = TransactionRepositoryImpl()

    private val getUserDataUseCase = GetUserDataUseCase(userRepository)
    private val getAccountUseCase = GetAccountUseCase(accountRepository)
    private val getTransactionsUseCase = GetTransactionsUseCase(transactionRepository)
    private val getUserNamesUseCase = GetUserNamesUseCase(userRepository)

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

    // Saldo leído desde el nodo 'accounts'
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
        getUserDataUseCase(documentId) { user ->
            fullName = user?.fullName ?: ""
            isLoading = false
        }

        getAccountUseCase(documentId) { account ->
            balance = account?.balance ?: 0.0
            accountNumber = account?.accountNumber ?: ""
        }
    }

    fun loadTransactions(documentId: String) {
        if (documentId.isEmpty()) {
            transactions = emptyList()
            transactionsUi = emptyList()
            return
        }

        getUserNamesUseCase { names ->
            userNames = names
            getTransactionsUseCase(documentId) { lista ->
                transactions = lista
                transactionsUi = lista.map { mapToUi(it, documentId) }
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
