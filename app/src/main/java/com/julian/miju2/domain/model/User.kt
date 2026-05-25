package com.julian.miju2.domain.model

data class User(
    val documentId: String,
    val fullName: String,
    val email: String,
    val cellphoneNumber: String,
    val password: String? = null
)
