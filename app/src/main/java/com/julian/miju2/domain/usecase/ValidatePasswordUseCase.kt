package com.julian.miju2.domain.usecase

import com.julian.miju2.R
import javax.inject.Inject

class ValidatePasswordUseCase @Inject constructor() {
    operator fun invoke(
        password: String,
        documentId: String = "",
        cellphoneNumber: String = ""
    ): Int? {
        val isAllDigits = password.all { it.isDigit() }
        val isIdentical = password.length == 6 && password.toSet().size == 1
        val isAscending = password in listOf("012345", "123456", "234567", "345678", "456789")
        val isDescending = password in listOf("987654", "876543", "765432", "654321", "543210")
        
        val isAlternating = password.length == 6 && password.substring(0, 2).repeat(3) == password
        val isRepeatedBlock = password.length == 6 && password.substring(0, 3).repeat(2) == password

        val matchesId = documentId.length >= 6 && (documentId.take(6) == password || documentId.takeLast(6) == password)
        val matchesPhone = cellphoneNumber.length >= 6 && cellphoneNumber.takeLast(6) == password

        return when {
            password.length != 6 -> R.string.error_password_length_6
            !isAllDigits -> R.string.error_password_only_digits
            isIdentical -> R.string.error_password_identical_digits
            isAscending || isDescending -> R.string.error_password_consecutive
            isAlternating -> R.string.error_password_alternating_pattern
            isRepeatedBlock -> R.string.error_password_repeated_blocks
            matchesId -> R.string.error_password_id_match
            matchesPhone -> R.string.error_password_phone_match
            else -> null
        }
    }
}
