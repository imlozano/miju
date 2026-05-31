package com.julian.miju2.domain.usecase

import com.julian.miju2.domain.model.User
import com.julian.miju2.domain.repository.UserRepository

class RegisterUserUseCase(private val repository: UserRepository) {
    operator fun invoke(user: User, onResult: (Boolean, Int) -> Unit) {
        repository.registerUser(user, onResult)
    }
}
