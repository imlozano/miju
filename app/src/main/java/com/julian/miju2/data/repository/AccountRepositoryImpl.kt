package com.julian.miju2.data.repository

import com.julian.miju2.data.datasource.FirebaseAccountDataSource
import com.julian.miju2.domain.model.Account
import com.julian.miju2.domain.repository.AccountRepository
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val dataSource: FirebaseAccountDataSource = FirebaseAccountDataSource()
) : AccountRepository {

    override fun getAccount(documentId: String, onResult: (Account?) -> Unit) {
        dataSource.getAccount(documentId)
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val account = Account(
                        accountNumber = snapshot.child("accountNumber").value?.toString() ?: "",
                        accountType = snapshot.child("accountType").value?.toString() ?: "",
                        ownerId = snapshot.child("ownerId").value?.toString() ?: "",
                        balance = snapshot.child("balance").getValue(Double::class.java) ?: 0.0
                    )
                    onResult(account)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    override fun updateBalance(documentId: String, newBalance: Double, onResult: (Boolean) -> Unit) {
        dataSource.updateBalance(documentId, newBalance)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }
}
