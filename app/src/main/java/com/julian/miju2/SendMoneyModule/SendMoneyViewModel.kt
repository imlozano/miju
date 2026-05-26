package com.julian.miju2.SendMoneyModule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase

class SendMoneyViewModel : ViewModel() {
    private val accountsRef = FirebaseDatabase.getInstance().getReference("accounts")

    private val currencyFormat = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("es", "CO"))

    var recipientAccount by mutableStateOf("")
        private set

    var amount by mutableStateOf("")
        private set

    var concept by mutableStateOf("")
        private set

    var senderBalance by mutableStateOf(0.0)
        private set

    val formattedBalance: String
        get() = currencyFormat.format(senderBalance)

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
}
