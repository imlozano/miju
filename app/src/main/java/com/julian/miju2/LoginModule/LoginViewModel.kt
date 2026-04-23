package com.julian.miju2.LoginModule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel: ViewModel() {

    var email: String by mutableStateOf("")
        private set
    var password: String by mutableStateOf("")
        private set
    var rememberMe: Boolean by mutableStateOf(false)
        private set

    var passwordVisible: Boolean by mutableStateOf(false)
        private set





    fun onEmailChange(nuevoEmail: String){
        email = nuevoEmail
    }


    fun onPasswordChange(nuevoPassword: String){
        password = nuevoPassword
    }

    fun onRememberChange(nuevoValor: Boolean){
        rememberMe = nuevoValor
    }

    fun togglePasswordVisibility(){
        passwordVisible = !passwordVisible
    }
}