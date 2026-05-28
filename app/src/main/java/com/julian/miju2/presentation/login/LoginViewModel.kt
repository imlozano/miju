package com.julian.miju2.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.julian.miju2.R
import com.julian.miju2.data.repository.UserRepositoryImpl
import com.julian.miju2.domain.usecase.LoginUseCase

class LoginViewModel : ViewModel() {

    private val repository = UserRepositoryImpl()
    private val loginUseCase = LoginUseCase(repository)

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
    var loginErrorMessage: Int? by mutableStateOf(null)
        private set

    var isLoading: Boolean by mutableStateOf(false)
        private set
    var loginSuccess: Boolean by mutableStateOf(false)
        private set


    fun onDocumentIdChange(newDocumentId: String) {
        if (newDocumentId.all { it.isDigit() }) {
            documentId = newDocumentId
            documentIdError = null
            loginErrorMessage = null
        }
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
        passwordError = null
        loginErrorMessage = null
    }

    fun clearLoginError() { loginErrorMessage = null }

    fun onRememberChange(newValue: Boolean) {
        rememberMe = newValue
    }

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }

    fun onLoginClick() {
        if (!validateFormat()) return

        isLoading = true
        loginUseCase(documentId, password) { success, _, _ ->
            isLoading = false
            if (success) {
                loginSuccess = true
            } else {
                loginErrorMessage = R.string.login_error_invalid_credentials
            }
        }
    }

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
