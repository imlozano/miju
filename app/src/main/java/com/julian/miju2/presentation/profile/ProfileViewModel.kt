package com.julian.miju2.presentation.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.julian.miju2.data.repository.UserRepositoryImpl
import com.julian.miju2.domain.usecase.GetUserDataUseCase
import com.julian.miju2.domain.usecase.UpdatePasswordUseCase
import com.julian.miju2.domain.usecase.ValidatePasswordUseCase

class ProfileViewModel : ViewModel() {
    
    // Inyección manual de dependencias
    private val repository = UserRepositoryImpl()
    private val getUserDataUseCase = GetUserDataUseCase(repository)
    private val updatePasswordUseCase = UpdatePasswordUseCase(repository)
    private val validatePasswordUseCase = ValidatePasswordUseCase()

    private var currentDocumentId: String = ""

    // Estados de datos de usuario
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
    var passwordError by mutableStateOf<Int?>(null)
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
            }
            isLoading = false
        }
    }

    fun onNewPasswordChange(password: String) {
        newPassword = password
        passwordError = null
    }

    fun resetPasswordState() {
        passwordChangeSuccess = false
        passwordError = null
        newPassword = ""
    }

    fun onChangePasswordClick() {
        if (currentDocumentId.isEmpty()) return
        
        // VALIDACIÓN REUTILIZABLE: Usamos el mismo caso de uso que el registro
        val errorResId = validatePasswordUseCase(
            password = newPassword,
            documentId = currentDocumentId,
            cellphoneNumber = cellphoneNumber
        )

        if (errorResId != null) {
            passwordError = errorResId
            return
        }

        isChangingPassword = true
        updatePasswordUseCase(currentDocumentId, newPassword) { success, _ ->
            isChangingPassword = false
            if (success) {
                passwordChangeSuccess = true
                newPassword = ""
            } else {
                // Aquí podrías asignar un error de conexión genérico
            }
        }
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
}
