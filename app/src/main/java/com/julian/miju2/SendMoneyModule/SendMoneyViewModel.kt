package com.julian.miju2.SendMoneyModule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.julian.miju2.R

class SendMoneyViewModel : ViewModel() {
    private val accountsRef = FirebaseDatabase.getInstance().getReference("accounts")
    private val usersRef = FirebaseDatabase.getInstance().getReference("users")

    private val currencyFormat = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("es", "CO"))

    var recipientAccount by mutableStateOf("")
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

    val formattedBalance: String
        get() = currencyFormat.format(senderBalance)

    val amountValue: Double
        get() = amount.toDoubleOrNull() ?: 0.0

    fun onRecipientChange(value: String) {
        if (value.all { it.isDigit() }) recipientAccount = value
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

        accountsRef.child(documentId).child("balance").get().addOnSuccessListener { snapshot ->
            senderBalance = snapshot.getValue(Double::class.java) ?: 0.0
        }.addOnFailureListener {
            senderBalance = 0.0
        }
    }

    fun validateAndResolve(senderDocumentId: String, onValid: () -> Unit) {
        errorMessage = null

        if (recipientAccount.isBlank()) {
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

        isLoading = true
        accountsRef.get().addOnSuccessListener { snapshot ->
            val found = snapshot.children.firstOrNull { child ->
                child.child("accountNumber").value?.toString() == recipientAccount
            }
            if (found == null) {
                isLoading = false
                errorMessage = R.string.send_money_error_recipient_not_found
                return@addOnSuccessListener
            }

            val ownerId = found.child("ownerId").value?.toString() ?: (found.key ?: "")
            if (ownerId == senderDocumentId) {
                isLoading = false
                errorMessage = R.string.send_money_error_self
                return@addOnSuccessListener
            }

            recipientDocumentId = ownerId
            usersRef.child(ownerId).child("fullName").get().addOnSuccessListener { userSnap ->
                recipientName = userSnap.value?.toString() ?: ""
                isLoading = false
                onValid()
            }.addOnFailureListener {
                isLoading = false
                errorMessage = R.string.send_money_error_connection
            }
        }.addOnFailureListener {
            isLoading = false
            errorMessage = R.string.send_money_error_connection
        }
    }
}
