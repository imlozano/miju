package com.julian.miju2.domain.usecase

import com.julian.miju2.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class SaveUserNameUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(name: String) = repository.saveUserName(name)
}
