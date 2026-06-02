package com.julian.miju2.data.datasource

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class FirebaseAccountDataSource @Inject constructor() {

    private val accountsRef = FirebaseDatabase.getInstance().getReference("accounts")

    fun getAccount(documentId: String): Task<DataSnapshot> {
        return accountsRef.child(documentId).get()
    }

    fun updateBalance(documentId: String, newBalance: Double): Task<Void> {
        return accountsRef.child(documentId).child("balance").setValue(newBalance)
    }
}
