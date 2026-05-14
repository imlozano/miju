package com.julian.miju2.SignUpModule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.julian.miju2.R

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
    var cellphoneNumber by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var confirmPassword by mutableStateOf("")
        private set
    var acceptedTerms by mutableStateOf(false)
        private set

    // Estados de error (Int? para mostrar los textos del xml)
    var fullNameError by mutableStateOf<Int?>(null)
        private set
    var documentIdError by mutableStateOf<Int?>(null)
        private set
    var emailError by mutableStateOf<Int?>(null)
        private set
    var cellphoneNumberError by mutableStateOf<Int?>(null)
        private set
    var passwordError by mutableStateOf<Int?>(null)
        private set
    var confirmPasswordError by mutableStateOf<Int?>(null)
        private set
    var termsError by mutableStateOf<Int?>(null)
        private set

    // Estado de UI
    var isLoading by mutableStateOf(false)
        private set

    // Eventos
    fun onFullNameChange(newValue: String) {
        fullName = newValue
        fullNameError = null
    }

    fun onDocumentIdChange(newValue: String) {
        if (newValue.all { it.isDigit() } && newValue.length <= 10) {
            documentId = newValue
            documentIdError = null
        }
    }

    fun onEmailChange(newValue: String) {
        email = newValue
        emailError = null
    }

    fun onCellphoneNumberChange(newValue: String) {
        if (newValue.all { it.isDigit() }) {
            cellphoneNumber = newValue
            cellphoneNumberError = null
        }
    }

    fun onPasswordChange(newValue: String) {
        if (newValue.all { it.isDigit() } && newValue.length <= 6) {
            password = newValue
            passwordError = null
        }
    }

    fun onConfirmPasswordChange(newValue: String) {
        if (newValue.all { it.isDigit() } && newValue.length <= 6) {
            confirmPassword = newValue
            confirmPasswordError = null
        }
    }

    fun onTermsChange(newValue: Boolean) {
        acceptedTerms = newValue
        termsError = null
    }

    /**
     * Intento de registro con doble verificación de duplicados.
     * @param onResult Callback que devuelve (éxito: Boolean, mensajeResId: Int)
     */
    fun onSignUpClick(onResult: (Boolean, Int) -> Unit) {
        if (validateFields()) {
            isLoading = true
            
            // 1. Verificar si el documento ya existe
            database.child(documentId).get()
                .addOnSuccessListener { docSnapshot ->
                    if (docSnapshot.exists()) {
                        isLoading = false
                        documentIdError = R.string.error_document_invalid
                        onResult(false, R.string.error_document_invalid)
                    } else {
                        // 2. Si el documento es nuevo, verificar el email
                        database.orderByChild("email").equalTo(email).get()
                            .addOnSuccessListener { emailSnapshot ->
                                if (emailSnapshot.exists()) {
                                    isLoading = false
                                    emailError = R.string.error_email_exists
                                    onResult(false, R.string.error_email_exists)
                                } else {
                                    registerUser(onResult)
                                }
                            }
                            .addOnFailureListener {
                                isLoading = false
                                onResult(false, R.string.error_connection_failed)
                            }
                    }
                }
                .addOnFailureListener {
                    isLoading = false
                    onResult(false, R.string.error_connection_failed)
                }
        }
    }

    private fun registerUser(onResult: (Boolean, Int) -> Unit) {
        val user = mapOf(
            "fullName" to fullName,
            "documentId" to documentId,
            "email" to email,
            "cellphoneNumber" to cellphoneNumber,
            "password" to password
        )

        database.child(documentId).setValue(user)
            .addOnSuccessListener {
                isLoading = false
                onResult(true, R.string.signup_success)
            }
            .addOnFailureListener {
                isLoading = false
                onResult(false, R.string.error_register_failed)
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
            fullNameError = R.string.error_name_required
            isValid = false
        }

        // Validacion de documento de identidad
        if (documentId.length != 10) {
            documentIdError = R.string.error_document_length_10
            isValid = false
        } else {
            val nuipValue = documentId.toLongOrNull() ?: 0L
            if (nuipValue <= 1000000000L) {
                documentIdError = R.string.error_document_nuip_range
                isValid = false
            }
        }

        if (!email.matches(emailPattern)) {
            emailError = R.string.error_invalid_email
            isValid = false
        }

        if (cellphoneNumber.length < 10) {
            cellphoneNumberError = R.string.error_cellphone_short
            isValid = false
        }

        val isAllDigits = password.all { it.isDigit() }
        val isIdentical = password.length == 6 && password.toSet().size == 1
        val isAscending = password == "012345" || password == "123456" || password == "234567" || password == "345678" || password == "456789"
        val isDescending = password == "987654" || password == "876543" || password == "765432" || password == "654321" || password == "543210"
        
        // Patrón espejo/alterno (ej. 121212)
        val isAlternating = password.length == 6 && password.substring(0, 2).repeat(3) == password
        // Bloques repetidos (ej. 123123)
        val isRepeatedBlock = password.length == 6 && password.substring(0, 3).repeat(2) == password


        val matchesId = documentId.length >= 6 && (documentId.take(6) == password || documentId.takeLast(6) == password)

        val matchesPhone = cellphoneNumber.length >= 6 && cellphoneNumber.takeLast(6) == password

        when {
            password.length != 6 -> {
                passwordError = R.string.error_password_length_6
                isValid = false
            }
            !isAllDigits -> {
                passwordError = R.string.error_password_only_digits
                isValid = false
            }
            isIdentical -> {
                passwordError = R.string.error_password_identical_digits
                isValid = false
            }
            isAscending || isDescending -> {
                passwordError = R.string.error_password_consecutive
                isValid = false
            }
            isAlternating -> {
                passwordError = R.string.error_password_alternating_pattern
                isValid = false
            }
            isRepeatedBlock -> {
                passwordError = R.string.error_password_repeated_blocks
                isValid = false
            }
            matchesId -> {
                passwordError = R.string.error_password_id_match
                isValid = false
            }
            matchesPhone -> {
                passwordError = R.string.error_password_phone_match
                isValid = false
            }
        }

        if (password != confirmPassword) {
            confirmPasswordError = R.string.error_passwords_not_match
            isValid = false
        }
        
        if (!acceptedTerms) {
            termsError = R.string.error_terms_required
            isValid = false
        }

        return isValid
    }
}
