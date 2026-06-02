package com.julian.miju2.presentation.signUp

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.julian.miju2.R
import com.julian.miju2.data.repository.UserRepositoryImpl
import com.julian.miju2.domain.model.User
import com.julian.miju2.domain.usecase.CheckEmailUseCase
import com.julian.miju2.domain.usecase.GetUserDataUseCase
import com.julian.miju2.domain.usecase.ParseDocumentTextUseCase
import com.julian.miju2.domain.usecase.RegisterUserUseCase
import com.julian.miju2.domain.usecase.SaveOcrScanUseCase
import com.julian.miju2.domain.usecase.ValidatePasswordUseCase

class SignUpViewModel : ViewModel() {

    private val repository = UserRepositoryImpl()
    private val registerUserUseCase = RegisterUserUseCase(repository)
    private val checkEmailUseCase = CheckEmailUseCase(repository)
    private val validatePasswordUseCase = ValidatePasswordUseCase()
    private val getUserDataUseCase = GetUserDataUseCase(repository)
    private val parseDocumentTextUseCase = ParseDocumentTextUseCase()
    private val saveOcrScanUseCase = SaveOcrScanUseCase(repository)

    // Texto crudo del último escaneo OCR (para trazabilidad tras el registro).
    private var lastOcrRawText: String? = null

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
    var passwordVisible by mutableStateOf(false)
        private set

    var confirmPasswordVisible by mutableStateOf(false)
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

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }

    fun toggleConfirmPasswordVisibility() {
        confirmPasswordVisible = !confirmPasswordVisible
    }
    fun onTermsChange(newValue: Boolean) {
        acceptedTerms = newValue; termsError = null
    }

    /**
     * Recibe el texto crudo del OCR, lo parsea (heurístico) y AUTOCOMPLETA los
     * campos del formulario. El usuario puede revisar y corregir antes de registrarse.
     * También conserva el texto crudo para guardarlo como traza tras el registro.
     */
    fun applyOcrText(rawText: String) {
        lastOcrRawText = rawText
        val parsed = parseDocumentTextUseCase(rawText)

        if (parsed.fullName.isNotBlank()) {
            fullName = parsed.fullName
            fullNameError = null
        }
        // Reutiliza la validación de entrada existente (solo dígitos, máx. 10).
        if (parsed.documentId.isNotBlank()) {
            onDocumentIdChange(parsed.documentId.take(10))
        }
        Log.d("OCR", rawText)
    }

    fun onSignUpClick(onResult: (Boolean, Int) -> Unit) {
        if (validateFields()) {
            isLoading = true
            
            getUserDataUseCase(documentId) { existingUser ->
                if (existingUser != null) {
                    isLoading = false
                    documentIdError = R.string.error_document_invalid
                    onResult(false, R.string.error_document_invalid)
                } else {
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
                            registerUserUseCase(newUser) { success, messageId ->
                                isLoading = false
                                // Traza opcional del OCR tras un registro exitoso.
                                if (success) {
                                    lastOcrRawText?.let { raw ->
                                        saveOcrScanUseCase(documentId, raw)
                                    }
                                }
                                onResult(success, messageId)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
        
        if (fullName.isBlank()) {
            fullNameError = R.string.error_name_required; isValid = false
        }

        if (!documentId.all { it.isDigit() }) {
            documentIdError = R.string.error_document_numeric
            isValid = false
        } else if (documentId.length !in 6..10) {
            documentIdError = R.string.error_document_length
            isValid = false
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
