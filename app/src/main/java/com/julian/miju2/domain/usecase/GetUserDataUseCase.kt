package com.julian.miju2.domain.usecase

import com.julian.miju2.domain.model.User
import com.julian.miju2.domain.repository.UserRepository
import javax.inject.Inject

class GetUserDataUseCase @Inject constructor(private val repository: UserRepository) {
    operator fun invoke(documentId: String, onResult: (User?) -> Unit) {
        repository.getUserData(documentId, onResult)
    }
}
