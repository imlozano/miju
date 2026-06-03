package com.julian.miju2.data.repository

import com.julian.miju2.R
import com.julian.miju2.data.datasource.FirebaseUserDataSource
import com.julian.miju2.domain.model.User
import com.julian.miju2.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val dataSource: FirebaseUserDataSource
) : UserRepository {

    override fun getUserData(documentId: String, onResult: (User?) -> Unit) {
        dataSource.getUser(documentId)
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val user = User(
                        documentId = snapshot.child("documentId").value?.toString() ?: "",
                        fullName = snapshot.child("fullName").value?.toString() ?: "",
                        email = snapshot.child("email").value?.toString() ?: "",
                        cellphoneNumber = snapshot.child("cellphoneNumber").value?.toString() ?: ""
                    )
                    onResult(user)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    override fun registerUser(user: User, onResult: (Boolean, Int) -> Unit) {
        val userData = mapOf(
            "documentId" to user.documentId,
            "fullName" to user.fullName,
            "email" to user.email,
            "cellphoneNumber" to user.cellphoneNumber,
            "password" to user.password
        )

        dataSource.saveUser(user.documentId, userData)
            .addOnSuccessListener {
                val accountData = mapOf(
                    "accountNumber" to ("03" + user.documentId),
                    "accountType" to "savings",
                    "ownerId" to user.documentId,
                    "balance" to 0.0
                )
                
                dataSource.saveAccount(user.documentId, accountData)
                    .addOnSuccessListener {
                        onResult(true, R.string.signup_success)
                    }
                    .addOnFailureListener {
                        onResult(false, R.string.error_register_failed)
                    }
            }
            .addOnFailureListener {
                onResult(false, R.string.error_register_failed)
            }
    }

    override fun updatePassword(documentId: String, newPassword: String, onResult: (Boolean, Int) -> Unit) {
        dataSource.updateField(documentId, "password", newPassword)
            .addOnSuccessListener {
                onResult(true, R.string.success_title)
            }
            .addOnFailureListener {
                onResult(false, R.string.error_connection_failed)
            }
    }

    override fun updateUserData(documentId: String, updates: Map<String, Any?>, onResult: (Boolean, Int) -> Unit) {
        dataSource.updateFields(documentId, updates)
            .addOnSuccessListener {
                onResult(true, R.string.success_title)
            }
            .addOnFailureListener {
                onResult(false, R.string.error_connection_failed)
            }
    }

    override fun login(documentId: String, password: String, onResult: (Boolean, Int, User?) -> Unit) {
        dataSource.getUser(documentId)
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) {
                    onResult(false, R.string.login_error_invalid_credentials, null)
                    return@addOnSuccessListener
                }

                val dbPassword = snapshot.child("password").value?.toString()
                if (dbPassword == password) {
                    val user = User(
                        documentId = documentId,
                        fullName = snapshot.child("fullName").value?.toString() ?: "",
                        email = snapshot.child("email").value?.toString() ?: "",
                        cellphoneNumber = snapshot.child("cellphoneNumber").value?.toString() ?: ""
                    )
                    onResult(true, R.string.success_title, user)
                } else {
                    onResult(false, R.string.login_error_invalid_credentials, null)
                }
            }
            .addOnFailureListener {
                onResult(false, R.string.error_connection_failed, null)
            }
    }

    override fun isEmailRegistered(email: String, onResult: (Boolean) -> Unit) {
        dataSource.getUserByEmail(email)
            .addOnSuccessListener { snapshot ->
                onResult(snapshot.exists())
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    override fun saveOcrScan(documentId: String, rawText: String, timestamp: Long) {
        val ocrData = mapOf(
            "rawText" to rawText,
            "timestamp" to timestamp
        )
        dataSource.saveOcrScan(documentId, ocrData)
    }

    override fun getAllUsers(onResult: (List<User>) -> Unit) {
        dataSource.getAllUsers()
            .addOnSuccessListener { snapshot ->
                val users = snapshot.children.map { child ->
                    User(
                        documentId = child.child("documentId").value?.toString() ?: (child.key ?: ""),
                        fullName = child.child("fullName").value?.toString() ?: "",
                        email = child.child("email").value?.toString() ?: "",
                        cellphoneNumber = child.child("cellphoneNumber").value?.toString() ?: ""
                    )
                }
                onResult(users)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}
