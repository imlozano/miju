package com.julian.miju2.LoginModule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginViewModel: ViewModel() {

    // Declaración que me permite guardar información que traigo de firebase
//    private lateinit var database: DatabaseReference

    var documentId: String by mutableStateOf("")
        private set
    var password: String by mutableStateOf("")
        private set
    var rememberMe: Boolean by mutableStateOf(false)
        private set

    var passwordVisible: Boolean by mutableStateOf(false)
        private set

    var documentIdError: String? by mutableStateOf(null)
        private set
    var passwordError: String? by mutableStateOf(null)
        private set

    var isLoading: Boolean by mutableStateOf(false)
        private set
    var loginSuccess: Boolean by mutableStateOf(false)
        private set

//    private lateinit var database: DatabaseReference
private val database = FirebaseDatabase.getInstance().getReference("users")




    // Eventos de la UI
    fun onDocumentIdChange(newDocumentId: String) {
        if (newDocumentId.all { it.isDigit() }) {
            documentId = newDocumentId
            documentIdError = null
        }
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
        passwordError = null
    }

    fun onRememberChange(newValue: Boolean) {
        rememberMe = newValue
    }

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }



//    fun login(documentNumber: String, password: String){
//        // Instancia de dfirebase database que saca la referencia users
//        database =
//
//    }

    // Lógica principal: validar formato y luego consultar Firebase

    fun onLoginClick() {
        if (!validateFormat()) return

//        database = FirebaseDatabase.getInstance().getReference("users")

        isLoading = true

        database.child(documentId).get()
            .addOnSuccessListener { snapshot ->
                isLoading = false

                if (!snapshot.exists()) {
                    documentIdError = "Usuario no encontrado"
                    return@addOnSuccessListener
                }

                val dbPassword = snapshot.child("password").value?.toString()

                if (dbPassword == password) {
                    loginSuccess = true
//                    Log.d(null, "Login exitoso: documentId=$documentId")
                    println("Login exitoso: documentId=$documentId")
                } else {
                    passwordError = "Contraseña incorrecta"
                }
            }
            .addOnFailureListener {
                isLoading = false
                passwordError = "Error de conexión. Intenta de nuevo."
            }
    }

    // Validación de formato (solo local)
    private fun validateFormat(): Boolean {
        val docError = when {
            documentId.isBlank() -> "El documento es obligatorio"
            documentId.length < 6 -> "Debe tener al menos 6 dígitos"
            documentId.length > 10 -> "Máximo 10 dígitos"
            else -> null
        }

        val passError = when {
            password.isBlank() -> "La contraseña es obligatoria"
            password.length < 6 -> "Debe tener al menos 6 caracteres"
            else -> null
        }

        documentIdError = docError
        passwordError = passError

        return docError == null && passError == null
    }
}