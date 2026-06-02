package com.julian.miju2.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julian.miju2.R
import com.julian.miju2.domain.usecase.ClearRememberedDocumentUseCase
import com.julian.miju2.domain.usecase.GetRememberedDocumentUseCase
import com.julian.miju2.domain.usecase.LoginUseCase
import com.julian.miju2.domain.usecase.SaveRememberedDocumentUseCase
import com.julian.miju2.domain.usecase.SaveUserNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val getRememberedDocumentUseCase: GetRememberedDocumentUseCase,
    private val saveRememberedDocumentUseCase: SaveRememberedDocumentUseCase,
    private val clearRememberedDocumentUseCase: ClearRememberedDocumentUseCase,
    private val saveUserNameUseCase: SaveUserNameUseCase
) : ViewModel() {

    var documentId: String by mutableStateOf("")
        private set
    var password: String by mutableStateOf("")
        private set
    var rememberMe: Boolean by mutableStateOf(false)
        private set

    var passwordVisible: Boolean by mutableStateOf(false)
        private set

    var documentIdError: Int? by mutableStateOf(null)
        private set
    var passwordError: Int? by mutableStateOf(null)
        private set
    var loginErrorMessage: Int? by mutableStateOf(null)
        private set

    var isLoading: Boolean by mutableStateOf(false)
        private set
    var loginSuccess: Boolean by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            getRememberedDocumentUseCase().firstOrNull()?.let { saved ->
                if (saved.isNotBlank()) {
                    documentId = saved
                    rememberMe = true
                }
            }
        }
    }

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
        loginUseCase(documentId, password) { success, _, user ->
            isLoading = false
            if (success) {
                viewModelScope.launch {
                    if (rememberMe) {
                        saveRememberedDocumentUseCase(documentId)
                    } else {
                        clearRememberedDocumentUseCase()
                    }
                    user?.let { saveUserNameUseCase(it.fullName) }
                }
                loginSuccess = true
            } else {
                loginErrorMessage = R.string.login_error_invalid_credentials
            }
        }
    }

    private fun validateFormat(): Boolean {
        val docError = when {
            documentId.isBlank() -> R.string.login_error_document_required
            documentId.length < 6 -> R.string.login_error_document_min
            documentId.length > 10 -> R.string.login_error_document_max
            else -> null
        }

        val passError = when {
            password.isBlank() -> R.string.login_error_password_required
            password.length < 6 -> R.string.login_error_password_min
            else -> null
        }

        documentIdError = docError
        passwordError = passError

        return docError == null && passError == null
    }
}
