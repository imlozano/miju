package com.julian.miju2.DashboardModule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase

class DashboardViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().getReference("users")

    val initial: String
        get() = fullName.trim()
            .firstOrNull { it.isLetterOrDigit() }
            ?.uppercaseChar()
            ?.toString()
            ?: "?"
    var fullName by mutableStateOf("")
        private set

    val displayName: String
        get() = fullName.trim().ifBlank { "Usuario" }
    var isLoading by mutableStateOf(false)
        private set

    fun loadUserData(documentId: String) {
        if (documentId.isEmpty()) {
            fullName = ""
            isLoading = false
            return
        }

        isLoading = true
        database.child(documentId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                fullName = snapshot.child("fullName").value?.toString() ?: ""
            }
            isLoading = false
        }.addOnFailureListener {
            isLoading = false
        }
    }


}
