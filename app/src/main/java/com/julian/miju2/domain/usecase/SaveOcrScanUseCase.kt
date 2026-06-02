package com.julian.miju2.domain.usecase

import com.julian.miju2.domain.repository.UserRepository
import javax.inject.Inject

class SaveOcrScanUseCase @Inject constructor(private val repository: UserRepository) {
    operator fun invoke(documentId: String, rawText: String) {
        repository.saveOcrScan(documentId, rawText, System.currentTimeMillis())
    }
}
