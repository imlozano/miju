package com.julian.miju2.data.repository

import com.julian.miju2.data.datasource.UserPreferencesDataSource
import com.julian.miju2.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataSource: UserPreferencesDataSource
) : UserPreferencesRepository {

    override val rememberedDocumentId: Flow<String?> = dataSource.rememberedDocumentId
    override val userName: Flow<String?> = dataSource.userName

    override suspend fun saveRememberedDocument(id: String) =
        dataSource.saveRememberedDocument(id)

    override suspend fun clearRememberedDocument() =
        dataSource.clearRememberedDocument()

    override suspend fun saveUserName(name: String) =
        dataSource.saveUserName(name)

    override suspend fun clearUserName() =
        dataSource.clearUserName()
}
