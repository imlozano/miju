package com.julian.miju2.domain.usecase

import com.julian.miju2.domain.model.User
import com.julian.miju2.domain.repository.UserRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(private val repository: UserRepository) {
    operator fun invoke(user: User, onResult: (Boolean, Int) -> Unit) {
        repository.registerUser(user, onResult)
    }
}
