package com.julian.miju2.data.datasource

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

class FirebaseUserDataSource {

    private val database = FirebaseDatabase.getInstance().getReference("users")
    private val accountsDatabase = FirebaseDatabase.getInstance().getReference("accounts")

    fun getUser(documentNumber: String): Task<DataSnapshot> {
        return database.child(documentNumber).get()
    }

    fun saveUser(documentNumber: String, userData: Map<String, Any?>): Task<Void> {
        return database.child(documentNumber).setValue(userData)
    }

    fun saveAccount(documentNumber: String, accountData: Map<String, Any?>): Task<Void> {
        return accountsDatabase.child(documentNumber).setValue(accountData)
    }

    fun updateField(documentNumber: String, field: String, value: Any): Task<Void> {
        return database.child(documentNumber).child(field).setValue(value)
    }

    fun getUserByEmail(email: String): Task<DataSnapshot> {
        return database.orderByChild("email").equalTo(email).get()
    }
}
