package com.julian.miju2.domain.usecase

import com.julian.miju2.domain.repository.UserRepository
import javax.inject.Inject

class CheckEmailUseCase @Inject constructor(private val repository: UserRepository) {
    operator fun invoke(email: String, onResult: (Boolean) -> Unit) {
        repository.isEmailRegistered(email, onResult)
    }
}
