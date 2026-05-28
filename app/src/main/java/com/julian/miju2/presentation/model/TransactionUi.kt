package com.julian.miju2.presentation.model

data class TransactionUi(
    val transactionId: String,
    val counterparty: String,
    val subtitle: String,
    val amountText: String,
    val isIncoming: Boolean
)
