package com.julian.miju2.domain.repository

import com.julian.miju2.domain.model.Account

interface AccountRepository {
    fun getAccount(documentId: String, onResult: (Account?) -> Unit)

    fun updateBalance(documentId: String, newBalance: Double, onResult: (Boolean) -> Unit)
}
