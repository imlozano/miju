package com.julian.miju2.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.julian.miju2.presentation.camera.CameraScannerScreen
import com.julian.miju2.presentation.dashboard.DashboardScreen
import com.julian.miju2.presentation.login.LoginScreen
import com.julian.miju2.presentation.profile.ProfileScreen
import com.julian.miju2.presentation.signUp.SignUpScreen
import com.julian.miju2.presentation.sendmoney.SendMoneyScreen
import com.julian.miju2.presentation.transactions.TransactionsScreen
import com.julian.miju2.ui.theme.ThemeMode


@Composable
fun AppNavigation(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("signup"){
            SignUpScreen(navController)
        }

        composable("camera-scanner") {
            CameraScannerScreen(
                onTextScanned = { rawText ->
                    // Devuelve el texto crudo al backstack anterior (signup) y vuelve.
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("ocr_raw_text", rawText)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("login"){
            LoginScreen(navController = navController)
        }

        composable(
            route = "profile/" + "{documentId}",
            arguments = listOf(navArgument("documentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val documentId = backStackEntry.arguments?.getString("documentId") ?: ""
            ProfileScreen(
                navController = navController,
                documentId = documentId,
                themeMode = themeMode,
                onThemeModeChange = onThemeModeChange
            )
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

        composable(
            route = "send-money/{documentId}",
            arguments = listOf(navArgument("documentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val documentId = backStackEntry.arguments?.getString("documentId") ?: ""
            SendMoneyScreen(navController = navController, documentId = documentId)
        }
    }
}
