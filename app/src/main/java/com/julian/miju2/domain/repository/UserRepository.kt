package com.julian.miju2.domain.repository

import com.julian.miju2.domain.model.User

interface UserRepository {
    // Para obtener los datos del perfil
    fun getUserData(documentId: String, onResult: (User?) -> Unit)

    // Para el registro (Usa tu patrón: éxito y ID del mensaje)
    fun registerUser(user: User, onResult: (Boolean, Int) -> Unit)

    // Para el cambio de contraseña
    fun updatePassword(documentId: String, newPassword: String, onResult: (Boolean, Int) -> Unit)

    // Para el login (Retorna éxito, ID del mensaje y el usuario si es correcto)
    fun login(documentId: String, password: String, onResult: (Boolean, Int, User?) -> Unit)
}
