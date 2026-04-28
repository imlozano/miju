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
            
            // Creamos un objeto con la información del usuario
            val user = mapOf(
                "fullName" to fullName,
                "email" to email,
                "password" to password // Nota: En producción usar Firebase Auth para seguridad
            )

            // Usamos el email (reemplazando puntos por comas ya que Firebase no permite puntos en keys)
            // O podrías usar un ID único generado con .push()
            val userId = email.replace(".", ",")

            database.child(userId).setValue(user)
                .addOnSuccessListener {
                    isLoading = false
                    signUpSuccess = true
                    println("Registro exitoso para: $email")
                }
                .addOnFailureListener {
                    isLoading = false
                    emailError = "Error al registrar: ${it.message}"
                }
        }
    }

    fun onOpenCamera() {
        // TODO: Lógica para abrir la cámara
        println("Abriendo cámara para verificación de ID")
    }

    private fun validateFields(): Boolean {
        var isValid = true
        
        if (fullName.isBlank()) {
            fullNameError = "El nombre es obligatorio"
            isValid = false
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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
            // Se podría añadir un error específico para el checkbox si fuera necesario
            isValid = false
        }

        return isValid
    }
}
