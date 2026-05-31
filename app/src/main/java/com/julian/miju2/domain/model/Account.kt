package com.julian.miju2.domain.model

data class Account(
    val accountNumber: String = "",
    val accountType: String = "",
    val ownerId: String = "",
    val balance: Double = 0.0
)
