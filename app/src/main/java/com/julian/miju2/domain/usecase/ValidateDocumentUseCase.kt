package com.julian.miju2.domain.usecase

import com.julian.miju2.R
import javax.inject.Inject

class ValidateDocumentUseCase @Inject constructor() {
    operator fun invoke(documentId: String): Int? {
        if (!documentId.all { it.isDigit() }) {
            return R.string.error_document_numeric
        }
        if (documentId.length !in 6..10) {
            return R.string.error_document_length
        }
        if (documentId.length == 10 && (documentId.toLongOrNull() ?: 0L) <= 1_000_000_000L) {
            return R.string.error_document_nuip_range
        }
        return null
    }
}
