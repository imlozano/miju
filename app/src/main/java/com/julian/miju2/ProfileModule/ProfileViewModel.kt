package com.julian.miju2.ProfileModule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase

class ProfileViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().getReference("users")

    var fullName by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var cellphoneNumber by mutableStateOf("")
        private set
    var isLoading by mutableStateOf(false)
        private set

    var navigateToLogin by mutableStateOf(false)
        private set

    fun loadUserData(documentId: String) {
        if (documentId.isEmpty()) return
        
        isLoading = true
        database.child(documentId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                fullName = snapshot.child("fullName").value?.toString() ?: ""
                email = snapshot.child("email").value?.toString() ?: ""
                cellphoneNumber = snapshot.child("cellphoneNumber").value?.toString() ?: ""
            }
            isLoading = false
        }.addOnFailureListener {
            isLoading = false
        }
    }
    
    fun onLogoutClick() {
        fullName = ""
        email = ""
        cellphoneNumber = ""
        navigateToLogin = true
    }

    fun onNavigationHandled() {
        navigateToLogin = false
    }
    
    fun onChangePasswordClick() {
        // TODO: Lógica para cambiar contraseña
    }
}
