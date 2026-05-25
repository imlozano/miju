package com.julian.miju2.DashboardModule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase

class DashboardViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().getReference("users")
    private val accountsRef = FirebaseDatabase.getInstance().getReference("accounts")

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

    var monthlyChangePercent by mutableStateOf(2.4)
        private set

    val formattedBalance: String
        get() = java.text.NumberFormat
            .getCurrencyInstance(java.util.Locale("es", "CO"))
            .format(balance)

    val monthlyChangeText: String
        get() = if (monthlyChangePercent >= 0) {
            "+$monthlyChangePercent%"
        } else {
            "$monthlyChangePercent%"
        }

    fun loadUserData(documentId: String) {
        if (documentId.isEmpty()) {
            fullName = ""
            balance = 0.0
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

        accountsRef.child(documentId).child("balance").get().addOnSuccessListener { snapshot ->
            balance = snapshot.getValue(Double::class.java) ?: 0.0
        }.addOnFailureListener {
            balance = 0.0
        }
    }


}
