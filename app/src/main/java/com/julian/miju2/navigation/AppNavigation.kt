package com.julian.miju2.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.julian.miju2.LoginModule.LoginScreen
import com.julian.miju2.SignUpModule.SignUpScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "signup"
    ) {

        composable("signup"){
            SignUpScreen(navController)
        }

        composable("login"){
            LoginScreen()
        }

    }
}
