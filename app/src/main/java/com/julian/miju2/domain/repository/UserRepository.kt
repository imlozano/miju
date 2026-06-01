package com.julian.miju2.domain.repository

import com.julian.miju2.domain.model.User

interface UserRepository {
    fun getUserData(documentId: String, onResult: (User?) -> Unit)

    fun registerUser(user: User, onResult: (Boolean, Int) -> Unit)

    fun updatePassword(documentId: String, newPassword: String, onResult: (Boolean, Int) -> Unit)

    fun login(documentId: String, password: String, onResult: (Boolean, Int, User?) -> Unit)
    fun isEmailRegistered(email: String, onResult: (Boolean) -> Unit)

    fun getAllUsers(onResult: (List<User>) -> Unit)

    /* Guarda en users/{documentId}/ocrScan el texto crudo del OCR para trazabilidad. */
    fun saveOcrScan(documentId: String, rawText: String, timestamp: Long)
}
