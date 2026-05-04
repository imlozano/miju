package com.julian.miju2.SignUpModule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase

class SignUpViewModel : ViewModel() {

    // Referencia a Firebase Realtime Database
    private val database = FirebaseDatabase.getInstance().getReference("users")

    // Estados de los campos
    var fullName by mutableStateOf("")
        private set
    var documentId by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var confirmPassword by mutableStateOf("")
        private set
    var acceptedTerms by mutableStateOf(false)
        private set

    // Estados de error
    var fullNameError by mutableStateOf<String?>(null)
        private set
    var documentIdError by mutableStateOf<String?>(null)
        private set
    var emailError by mutableStateOf<String?>(null)
        private set
    var passwordError by mutableStateOf<String?>(null)
        private set
    var confirmPasswordError by mutableStateOf<String?>(null)
        private set

    // Estado de UI
    var isLoading by mutableStateOf(false)
        private set
    var signUpSuccess by mutableStateOf(false)
        private set

    // Eventos
    fun onFullNameChange(newValue: String) {
        fullName = newValue
        fullNameError = null
    }

    fun onDocumentIdChange(newValue: String) {
        if (newValue.all { it.isDigit() }) {
            documentId = newValue
            documentIdError = null
        }
    }

    fun onEmailChange(newValue: String) {
        email = newValue
        emailError = null
    }

    fun onPasswordChange(newValue: String) {
        password = newValue
        passwordError = null
    }

    fun onConfirmPasswordChange(newValue: String) {
        confirmPassword = newValue
        confirmPasswordError = null
    }

    fun onTermsChange(newValue: Boolean) {
        acceptedTerms = newValue
    }

    fun onSignUpClick() {
        if (validateFields()) {
            isLoading = true
            
            // 1. Verificar si el email ya existe en algún usuario
            database.orderByChild("email").equalTo(email).get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {

                        isLoading = false
                        emailError = "El correo ya existe, usa uno nuevo"
                    } else {

                        registerUser()
                    }
                }
                .addOnFailureListener {
                    isLoading = false
                    emailError = "Error al verificar correo: ${it.message}"
                }
        }
    }

    private fun registerUser() {
        val user = mapOf(
            "fullName" to fullName,
            "documentId" to documentId,
            "email" to email,
            "password" to password
        )

        // documentId como llave
        database.child(documentId).setValue(user)
            .addOnSuccessListener {
                isLoading = false
                signUpSuccess = true
                println("Registro exitoso para: $documentId")
            }
            .addOnFailureListener {
                isLoading = false
                documentIdError = "Error al registrar: ${it.message}"
            }
    }

    fun onOpenCamera() {
        // TODO: Lógica para abrir la cámara
        println("Abriendo cámara para verificación de ID")
    }

    private fun validateFields(): Boolean {
        var isValid = true

        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
        
        if (fullName.isBlank()) {
            fullNameError = "El nombre es obligatorio"
            isValid = false
        }

        if (documentId.length < 6) {
            documentIdError = "El documento debe tener al menos 6 dígitos"
            isValid = false
        }

        if (!email.matches(emailPattern)) {
            emailError = "Email no válido"
            isValid = false
        }

        if (password.length < 6) {
            passwordError = "Mínimo 6 caracteres"
            isValid = false
        }

        if (password != confirmPassword) {
            confirmPasswordError = "Las contraseñas no coinciden"
            isValid = false
        }
        
        if (!acceptedTerms) {
            isValid = false
        }

        return isValid
    }
}
