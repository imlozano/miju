package com.julian.miju2.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.julian.miju2.DashboardModule.DashboardScreen
import com.julian.miju2.LoginModule.LoginScreen
import com.julian.miju2.presentation.profile.ProfileScreen
import com.julian.miju2.presentation.signUp.SignUpScreen
import com.julian.miju2.TransactionsModule.TransactionsScreen


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
            LoginScreen(navController = navController)
        }

        composable(
            route = "profile/{documentId}",
            arguments = listOf(navArgument("documentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val documentId = backStackEntry.arguments?.getString("documentId") ?: ""
            ProfileScreen(navController = navController, documentId = documentId)
        }

        composable(
            route = "dashboard/{documentId}",
            arguments = listOf(navArgument("documentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val documentId = backStackEntry.arguments?.getString("documentId") ?: ""
            DashboardScreen(navController = navController, documentId = documentId)
        }

        composable(
            route = "transactions/{documentId}",
            arguments = listOf(navArgument("documentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val documentId = backStackEntry.arguments?.getString("documentId") ?: ""
            TransactionsScreen(navController = navController, documentId = documentId)
        }
    }
}
