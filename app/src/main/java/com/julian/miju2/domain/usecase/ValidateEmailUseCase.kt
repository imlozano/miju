package com.julian.miju2.domain.usecase

import com.julian.miju2.R
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor() {
    private val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()

    operator fun invoke(email: String): Int? {
        if (!email.matches(emailPattern)) {
            return R.string.error_invalid_email
        }
        return null
    }
}
