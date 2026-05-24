package com.julian.miju2.ProfileModule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase

class ProfileViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().getReference("users")
    private var currentDocumentId: String = ""

    var fullName by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var cellphoneNumber by mutableStateOf("")
        private set
    var isLoading by mutableStateOf(false)
        private set

    // Estados para el cambio de contraseña
    var newPassword by mutableStateOf("")
        private set
    var isChangingPassword by mutableStateOf(false)
        private set
    var passwordChangeSuccess by mutableStateOf(false)
        private set
    var passwordError by mutableStateOf<String?>(null)
        private set

    var navigateToLogin by mutableStateOf(false)
        private set

    fun loadUserData(documentId: String) {
        if (documentId.isEmpty()) return
        currentDocumentId = documentId
        
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

    fun onNewPasswordChange(password: String) {
        newPassword = password
        passwordError = null
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

    fun resetPasswordState() {
        passwordChangeSuccess = false
        passwordError = null
        newPassword = ""
    }
    
    fun onChangePasswordClick() {
        if (currentDocumentId.isEmpty()) return
        

        if (newPassword.length < 6) {
            passwordError = "La contraseña debe tener al menos 6 caracteres"
            return
        }

        isChangingPassword = true
        passwordError = null


        database.child(currentDocumentId).child("password").setValue(newPassword)
            .addOnSuccessListener {
                isChangingPassword = false
                passwordChangeSuccess = true
                newPassword = ""
            }
            .addOnFailureListener {
                isChangingPassword = false
                passwordError = "Error al conectar con el servidor"
            }
    }
}
