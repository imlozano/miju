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
    private val transactionsRef = FirebaseDatabase.getInstance().getReference("transactions")

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

        accountsRef.child(documentId).child("balance").get().addOnSuccessListener { snapshot ->
            senderBalance = snapshot.getValue(Double::class.java) ?: 0.0
        }.addOnFailureListener {
            senderBalance = 0.0
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

        isLoading = true

        if (recipientDocument == senderDocumentId) {
            isLoading = false
            errorMessage = R.string.send_money_error_self
            return
        }

        accountsRef.child(recipientDocument).get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                isLoading = false
                errorMessage = R.string.send_money_error_recipient_not_found
                return@addOnSuccessListener
            }

            recipientDocumentId = recipientDocument
            usersRef.child(recipientDocument).child("fullName").get().addOnSuccessListener { userSnap ->
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

    fun sendMoney(senderDocumentId: String) {
        isLoading = true
        errorMessage = null

        accountsRef.child(senderDocumentId).child("balance").get().addOnSuccessListener { snapshot ->
            val currentBalance = snapshot.getValue(Double::class.java) ?: 0.0
            if (amountValue > currentBalance) {
                isLoading = false
                errorMessage = R.string.send_money_error_insufficient
                return@addOnSuccessListener
            }

            accountsRef.child(recipientDocumentId).child("balance").get().addOnSuccessListener { snapshot2 ->
                val recipientBalance = snapshot2.getValue(Double::class.java) ?: 0.0
                val newSenderBalance = currentBalance - amountValue
                val newRecipientBalance = recipientBalance + amountValue

                accountsRef.child(senderDocumentId).child("balance").setValue(newSenderBalance).addOnSuccessListener {
                    accountsRef.child(recipientDocumentId).child("balance").setValue(newRecipientBalance).addOnSuccessListener {
                        val newRef = transactionsRef.push()
                        val tx = mapOf(
                            "transactionId" to (newRef.key ?: ""),
                            "from" to senderDocumentId,
                            "to" to recipientDocumentId,
                            "amount" to amountValue,
                            "date" to System.currentTimeMillis(),
                            "status" to "completed",
                            "concept" to concept
                        )
                        newRef.setValue(tx).addOnSuccessListener {
                            isLoading = false
                            sendSuccess = true
                        }.addOnFailureListener {
                            isLoading = false
                            errorMessage = R.string.send_money_error_connection
                        }
                    }.addOnFailureListener {
                        isLoading = false
                        errorMessage = R.string.send_money_error_connection
                    }
                }.addOnFailureListener {
                    isLoading = false
                    errorMessage = R.string.send_money_error_connection
                }
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
