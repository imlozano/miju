package com.julian.miju2.domain.usecase

import com.julian.miju2.R
import com.julian.miju2.domain.repository.UserRepository
import javax.inject.Inject

class CheckUserExistsUseCase @Inject constructor(private val repository: UserRepository) {

    operator fun invoke(documentId: String, onResult: (Boolean, Int?) -> Unit) {
        repository.getUserData(documentId) { user ->
            if (user != null) {
                onResult(true, R.string.error_document_invalid)
            } else {
                onResult(false, null)
            }
        }
    }
}
