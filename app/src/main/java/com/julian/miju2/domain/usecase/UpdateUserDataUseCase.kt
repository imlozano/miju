package com.julian.miju2.domain.usecase

import com.julian.miju2.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserDataUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(
        documentId: String,
        updates: Map<String, Any?>,
        onResult: (Boolean, Int) -> Unit
    ) {
        userRepository.updateUserData(documentId, updates, onResult)
    }
}
