package com.julian.miju2.presentation.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julian.miju2.R
import com.julian.miju2.domain.usecase.ClearUserNameUseCase
import com.julian.miju2.domain.usecase.GetUserDataUseCase
import com.julian.miju2.domain.usecase.UpdatePasswordUseCase
import com.julian.miju2.domain.usecase.UpdateUserDataUseCase
import com.julian.miju2.domain.usecase.ValidateCellphoneUseCase
import com.julian.miju2.domain.usecase.ValidateEmailUseCase
import com.julian.miju2.domain.usecase.ValidatePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserDataUseCase: GetUserDataUseCase,
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val updateUserDataUseCase: UpdateUserDataUseCase,
    private val clearUserNameUseCase: ClearUserNameUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validateCellphoneUseCase: ValidateCellphoneUseCase
) : ViewModel() {

    private var currentDocumentId: String = ""

    private var originalEmail: String = ""
    private var originalCellphone: String = ""

    var fullName by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set
    var cellphoneNumber by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var newPassword by mutableStateOf("")
        private set
    var confirmNewPassword by mutableStateOf("")
        private set
    var isChangingPassword by mutableStateOf(false)
        private set
    var passwordChangeSuccess by mutableStateOf(false)
        private set
    var passwordError by mutableStateOf<Int?>(null)
        private set
    var confirmPasswordError by mutableStateOf<Int?>(null)
        private set

    var navigateToLogin by mutableStateOf(false)
        private set

    fun loadUserData(documentId: String) {
        if (documentId.isEmpty()) return
        currentDocumentId = documentId
        
        isLoading = true
        getUserDataUseCase(documentId) { user ->
            user?.let {
                fullName = it.fullName
                email = it.email
                cellphoneNumber = it.cellphoneNumber
                originalEmail = it.email
                originalCellphone = it.cellphoneNumber
            }
            isLoading = false
        }
    }

    fun onEmailChange(newValue: String){
        email = newValue
    }

    fun onCellphoneChange(newValue: String){
        cellphoneNumber = newValue
    }
    fun onNewPasswordChange(password: String) {
        newPassword = password
        passwordError = null
    }

    fun onConfirmNewPasswordChange(password: String) {
        confirmNewPassword = password
        confirmPasswordError = null
    }

    fun resetDataState(){
        email = originalEmail
        cellphoneNumber = originalCellphone
    }

    fun resetPasswordState() {
        passwordChangeSuccess = false
        passwordError = null
        confirmPasswordError = null
        newPassword = ""
        confirmNewPassword = ""
    }

    fun onChangePasswordClick(onResult: (Boolean, Int) -> Unit) {
        if (currentDocumentId.isEmpty()) return

        val errorResId = validatePasswordUseCase(
            password = newPassword,
            documentId = currentDocumentId,
            cellphoneNumber = cellphoneNumber
        )

        if (errorResId != null) {
            passwordError = errorResId
            return
        }

        if (newPassword != confirmNewPassword) {
            confirmPasswordError = R.string.error_passwords_not_match
            return
        }

        isChangingPassword = true
        updatePasswordUseCase(currentDocumentId, newPassword) { success, messageResId ->
            isChangingPassword = false
            if (success) {
                passwordChangeSuccess = true
                newPassword = ""
                confirmNewPassword = ""
            }
            onResult(success, messageResId)
        }
    }

    fun onChangeDataClick(onResult: (Boolean, Int) -> Unit) {
        if (currentDocumentId.isEmpty()) return

        val updates = mutableMapOf<String, Any>()

        if (email != originalEmail) {
            val emailErrorId = validateEmailUseCase(email)
            if (emailErrorId != null) {
                onResult(false, emailErrorId)
                return
            }
            updates["email"] = email
        }

        if (cellphoneNumber != originalCellphone) {
            val cellphoneNumberErrorId = validateCellphoneUseCase(cellphoneNumber)
            if (cellphoneNumberErrorId != null) {
                onResult(false, cellphoneNumberErrorId)
                return
            }
            updates["cellphoneNumber"] = cellphoneNumber
        }


        if (updates.isEmpty()) {
            onResult(false, R.string.profile_no_data_change)
            return
        }

        isLoading = true
        updateUserDataUseCase(currentDocumentId, updates) { success, messageResId ->
            isLoading = false
            if (success) {
                originalEmail = email
                originalCellphone = cellphoneNumber
            }
            onResult(success, messageResId)
        }
    }
    
    fun onLogoutClick() {
        viewModelScope.launch { clearUserNameUseCase() }
        fullName = ""
        email = ""
        cellphoneNumber = ""
        navigateToLogin = true
    }

    fun onNavigationHandled() {
        navigateToLogin = false
    }
}
