package com.julian.miju2.data.datasource

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

class FirebaseUserDataSource {

    private val database = FirebaseDatabase.getInstance().getReference("users")

    fun getUser(documentNumber: String): Task<DataSnapshot> {
        return database.child(documentNumber).get()
    }

    fun saveUser(documentNumber: String, userData: Map<String, String>): Task<Void> {
        return database.child(documentNumber).setValue(userData)
    }
}
