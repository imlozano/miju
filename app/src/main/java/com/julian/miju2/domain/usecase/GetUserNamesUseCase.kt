package com.julian.miju2.domain.usecase

import com.julian.miju2.domain.repository.UserRepository

class GetUserNamesUseCase(private val repository: UserRepository) {
    operator fun invoke(onResult: (Map<String, String>) -> Unit) {
        repository.getAllUsers { users ->
            onResult(users.associate { it.documentId to it.fullName })
        }
    }
}
