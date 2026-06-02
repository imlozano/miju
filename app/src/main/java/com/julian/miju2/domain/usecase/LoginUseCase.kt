package com.julian.miju2.domain.usecase

import com.julian.miju2.domain.model.User
import com.julian.miju2.domain.repository.UserRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repository: UserRepository) {
    operator fun invoke(documentId: String, password: String, onResult: (Boolean, Int, User?) -> Unit) {
        repository.login(documentId, password, onResult)
    }
}
