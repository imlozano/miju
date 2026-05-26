package com.julian.miju2.DashboardModule

data class Transaction(
    val transactionId: String = "",
    val from: String = "",
    val to: String = "",
    val amount: Double = 0.0,
    val date: Long = 0L,
    val status: String = ""
)

data class TransactionUi(
    val transactionId: String,
    val counterparty: String,
    val subtitle: String,
    val amountText: String,
    val isIncoming: Boolean
)