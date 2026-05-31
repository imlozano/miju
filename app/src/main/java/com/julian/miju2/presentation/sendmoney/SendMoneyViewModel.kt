package com.julian.miju2.presentation.sendmoney

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.julian.miju2.R
import com.julian.miju2.data.repository.AccountRepositoryImpl
import com.julian.miju2.data.repository.TransactionRepositoryImpl
import com.julian.miju2.data.repository.UserRepositoryImpl
import com.julian.miju2.domain.usecase.GetAccountUseCase
import com.julian.miju2.domain.usecase.GetUserDataUseCase
import com.julian.miju2.domain.usecase.SendMoneyUseCase

class SendMoneyViewModel : ViewModel() {

    private val accountRepository = AccountRepositoryImpl()
    private val userRepository = UserRepositoryImpl()
    private val transactionRepository = TransactionRepositoryImpl()

    private val getAccountUseCase = GetAccountUseCase(accountRepository)
    private val getUserDataUseCase = GetUserDataUseCase(userRepository)
    private val sendMoneyUseCase = SendMoneyUseCase(accountRepository, transactionRepository)

    private val currencyFormat = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("es", "CO"))

    var recipientDocument by mutableStateOf("")
        private set

    var amount by mutableStateOf("")
        private set

    var concept by mutableStateOf("")
        private set

    var senderBalance by mutableStateOf(0.0)
        private set

    var errorMessage by mutableStateOf<Int?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var recipientName by mutableStateOf("")
        private set

    var recipientDocumentId by mutableStateOf("")
        private set

    var sendSuccess by mutableStateOf(false)
        private set

    val formattedBalance: String
        get() = currencyFormat.format(senderBalance)

    val amountValue: Double
        get() = amount.toDoubleOrNull() ?: 0.0

    val formattedAmount: String
        get() = currencyFormat.format(amountValue)

    fun onRecipientChange(value: String) {
        if (value.all { it.isDigit() }) recipientDocument = value
    }

    fun onAmountChange(value: String) {
        if (value.all { it.isDigit() }) amount = value
    }

    fun onConceptChange(value: String) {
        concept = value
    }

    fun loadSenderBalance(documentId: String) {
        if (documentId.isEmpty()) {
            senderBalance = 0.0
            return
        }

        getAccountUseCase(documentId) { account ->
            senderBalance = account?.balance ?: 0.0
        }
    }

    fun validateAndResolve(senderDocumentId: String, onValid: () -> Unit) {
        errorMessage = null

        if (recipientDocument.isBlank()) {
            errorMessage = R.string.send_money_error_recipient_required
            return
        }
        if (amountValue <= 0.0) {
            errorMessage = R.string.send_money_error_amount_invalid
            return
        }
        if (amountValue > senderBalance) {
            errorMessage = R.string.send_money_error_insufficient
            return
        }
        if (recipientDocument == senderDocumentId) {
            errorMessage = R.string.send_money_error_self
            return
        }

        isLoading = true
        getAccountUseCase(recipientDocument) { account ->
            if (account == null) {
                isLoading = false
                errorMessage = R.string.send_money_error_recipient_not_found
                return@getAccountUseCase
            }

            recipientDocumentId = recipientDocument
            getUserDataUseCase(recipientDocument) { user ->
                recipientName = user?.fullName ?: ""
                isLoading = false
                onValid()
            }
        }
    }

    fun sendMoney(senderDocumentId: String) {
        isLoading = true
        errorMessage = null

        sendMoneyUseCase(
            senderId = senderDocumentId,
            recipientId = recipientDocumentId,
            amount = amountValue,
            concept = concept
        ) { success, messageResId ->
            isLoading = false
            if (success) {
                sendSuccess = true
            } else {
                errorMessage = messageResId
            }
        }
    }
}
