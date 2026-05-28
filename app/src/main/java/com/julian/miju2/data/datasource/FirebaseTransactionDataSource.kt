package com.julian.miju2.data.datasource

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

class FirebaseTransactionDataSource {

    private val txRef = FirebaseDatabase.getInstance().getReference("transactions")

    fun getAllTransactions(): Task<DataSnapshot> {
        return txRef.get()
    }

    fun createTransaction(tx: Map<String, Any?>): Task<Void> {
        return txRef.push().setValue(tx)
    }
}
