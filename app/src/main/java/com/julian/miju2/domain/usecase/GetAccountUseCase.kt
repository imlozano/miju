package com.julian.miju2.domain.usecase

import com.julian.miju2.domain.model.Account
import com.julian.miju2.domain.repository.AccountRepository

class GetAccountUseCase(private val repository: AccountRepository) {
    operator fun invoke(documentId: String, onResult: (Account?) -> Unit) {
        repository.getAccount(documentId, onResult)
    }
}
