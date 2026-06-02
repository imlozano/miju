package com.julian.miju2.domain.usecase

import com.julian.miju2.domain.repository.UserRepository
import javax.inject.Inject

class UpdatePasswordUseCase @Inject constructor(private val repository: UserRepository) {
    operator fun invoke(documentId: String, newPassword: String, onResult: (Boolean, Int) -> Unit) {
        repository.updatePassword(documentId, newPassword, onResult)
    }
}
