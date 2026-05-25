package com.julian.miju2.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.julian.miju2.R
import com.julian.miju2.domain.model.User
import com.julian.miju2.domain.repository.UserRepository

class UserRepositoryImpl : UserRepository {
    
    private val database = FirebaseDatabase.getInstance().getReference("users")

    override fun getUserData(documentId: String, onResult: (User?) -> Unit) {
        database.child(documentId).get()
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
        database.child(user.documentId).setValue(user)
            .addOnSuccessListener {
                onResult(true, R.string.signup_success)
            }
            .addOnFailureListener {
                onResult(false, R.string.error_register_failed)
            }
    }

    override fun updatePassword(documentId: String, newPassword: String, onResult: (Boolean, Int) -> Unit) {
        database.child(documentId).child("password").setValue(newPassword)
            .addOnSuccessListener {
                onResult(true, R.string.success_title) // O el string que prefieras
            }
            .addOnFailureListener {
                onResult(false, R.string.error_connection_failed)
            }
    }

    override fun login(documentId: String, password: String, onResult: (Boolean, Int, User?) -> Unit) {
        database.child(documentId).get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) {
                    onResult(false, R.string.error_document_invalid, null)
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
                    onResult(false, R.string.error_passwords_not_match, null)
                }
            }
            .addOnFailureListener {
                onResult(false, R.string.error_connection_failed, null)
            }
    }
}
