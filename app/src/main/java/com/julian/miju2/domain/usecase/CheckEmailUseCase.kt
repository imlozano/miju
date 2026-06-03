package com.julian.miju2.domain.usecase

import com.julian.miju2.R
import com.julian.miju2.domain.repository.UserRepository
import javax.inject.Inject

class CheckEmailUseCase @Inject constructor(private val repository: UserRepository) {

    operator fun invoke(email: String, onResult: (Boolean, Int?) -> Unit) {
        repository.isEmailRegistered(email) { exists ->
            if (exists) {
                onResult(false, R.string.error_email_exists)
            } else {
                onResult(true, null)
            }
        }
    }
}
