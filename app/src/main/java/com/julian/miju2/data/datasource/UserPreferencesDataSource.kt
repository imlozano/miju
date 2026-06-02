package com.julian.miju2.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private object Keys {
        val REMEMBERED_DOCUMENT = stringPreferencesKey("remembered_document")
        val USER_NAME = stringPreferencesKey("user_name")
    }

    val rememberedDocumentId: Flow<String?> =
        dataStore.data.map { prefs -> prefs[Keys.REMEMBERED_DOCUMENT] }

    val userName: Flow<String?> =
        dataStore.data.map { prefs -> prefs[Keys.USER_NAME] }

    suspend fun saveRememberedDocument(id: String) {
        dataStore.edit { prefs -> prefs[Keys.REMEMBERED_DOCUMENT] = id }
    }

    suspend fun clearRememberedDocument() {
        dataStore.edit { prefs -> prefs.remove(Keys.REMEMBERED_DOCUMENT) }
    }

    suspend fun saveUserName(name: String) {
        dataStore.edit { prefs -> prefs[Keys.USER_NAME] = name }
    }

    suspend fun clearUserName() {
        dataStore.edit { prefs -> prefs.remove(Keys.USER_NAME) }
    }
}
