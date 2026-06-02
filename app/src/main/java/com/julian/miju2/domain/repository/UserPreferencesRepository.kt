package com.julian.miju2.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val rememberedDocumentId: Flow<String?>
    val userName: Flow<String?>

    suspend fun saveRememberedDocument(id: String)
    suspend fun clearRememberedDocument()
    suspend fun saveUserName(name: String)
    suspend fun clearUserName()
}
