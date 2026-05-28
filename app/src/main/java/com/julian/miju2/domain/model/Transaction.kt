package com.julian.miju2.domain.model

data class Transaction(
    val transactionId: String = "",
    val from: String = "",
    val to: String = "",
    val amount: Double = 0.0,
    val date: Long = 0L,
    val status: String = "",
    val concept: String = ""
)
