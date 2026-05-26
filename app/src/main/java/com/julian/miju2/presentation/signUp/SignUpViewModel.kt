package com.julian.miju2.presentation.signUp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.julian.miju2.R
import com.julian.miju2.data.repository.UserRepositoryImpl
import com.julian.miju2.domain.model.User
import com.julian.miju2.domain.usecase.CheckEmailUseCase
import com.julian.miju2.domain.usecase.GetUserDataUseCase
import com.julian.miju2.domain.usecase.RegisterUserUseCase
import com.julian.miju2.domain.usecase.ValidatePasswordUseCase

class SignUpViewModel : ViewModel() {

    private val repository = UserRepositoryImpl()
    private val registerUserUseCase = RegisterUserUseCase(repository)
    private val checkEmailUseCase = CheckEmailUseCase(repository)
    private val validatePasswordUseCase = ValidatePasswordUseCase()
    private val getUserDataUseCase = GetUserDataUseCase(repository)

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

    var isLoading by mutableStateOf(false)
        private set

    fun onFullNameChange(newValue: String) {
        fullName = newValue; fullNameError = null
    }
    fun onDocumentIdChange(newValue: String) {
        if (newValue.all { it.isDigit() } && newValue.length <= 10) {
            documentId = newValue
            documentIdError = null
        }
    }
    fun onEmailChange(newValue: String) {
        email = newValue; emailError = null
    }
    fun onCellphoneNumberChange(newValue: String) {
        if (newValue.all { it.isDigit() }) {
            cellphoneNumber = newValue
            cellphoneNumberError = null
        }
    }
    fun onPasswordChange(newValue: String) {
        password = newValue; passwordError = null
    }
    fun onConfirmPasswordChange(newValue: String) {
        confirmPassword = newValue; confirmPasswordError = null
    }
    fun onTermsChange(newValue: Boolean) {
        acceptedTerms = newValue; termsError = null
    }

    fun onSignUpClick(onResult: (Boolean, Int) -> Unit) {
        if (validateFields()) {
            isLoading = true
            
            // Verificar si el documento ya existe
            getUserDataUseCase(documentId) { existingUser ->
                if (existingUser != null) {
                    isLoading = false
                    documentIdError = R.string.error_document_invalid
                    onResult(false, R.string.error_document_invalid)
                } else {
                    // Si el documento es nuevo, verificar el email
                    checkEmailUseCase(email) { exists ->
                        if (exists) {
                            isLoading = false
                            emailError = R.string.error_email_exists
                            onResult(false, R.string.error_email_exists)
                        } else {
                            val newUser = User(
                                documentId = documentId,
                                fullName = fullName,
                                email = email,
                                cellphoneNumber = cellphoneNumber,
                                password = password
                            )
                            registerUserUseCase(newUser, onResult)
                        }
                    }
                }
            }
        }
    }

    fun onOpenCamera() {
        println("Abriendo cámara para verificación de ID (Logcat)")
    }

    private fun validateFields(): Boolean {
        var isValid = true
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
        
        if (fullName.isBlank()) {
            fullNameError = R.string.error_name_required; isValid = false
        }

        if (documentId.length != 10) {
            documentIdError = R.string.error_document_length_10; isValid = false
        }

        if (!email.matches(emailPattern)) {
            emailError = R.string.error_invalid_email; isValid = false
        }

        if (cellphoneNumber.length < 10) {
            cellphoneNumberError = R.string.error_cellphone_short; isValid = false
        }

        passwordError = validatePasswordUseCase(password, documentId, cellphoneNumber)
        if (passwordError != null) isValid = false

        if (password != confirmPassword) {
            confirmPasswordError = R.string.error_passwords_not_match
            isValid = false
        }
        if (!acceptedTerms) {
            termsError = R.string.error_terms_required; isValid = false
        }

        return isValid
    }
}
