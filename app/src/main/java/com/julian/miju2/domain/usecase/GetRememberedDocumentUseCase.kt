package com.julian.miju2.domain.usecase

import com.julian.miju2.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRememberedDocumentUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<String?> = repository.rememberedDocumentId
}
