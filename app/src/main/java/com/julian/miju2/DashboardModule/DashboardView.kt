package com.julian.miju2.DashboardModule

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.julian.miju2.ui.theme.Background
import com.julian.miju2.ui.theme.Primary

@Composable
fun DashboardScreen(
    navController: NavController,
    documentId: String,
    viewModel: DashboardViewModel = viewModel()
) {
    Scaffold(
        containerColor = Background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Dashboard - $documentId",
                color = Primary
            )
        }
    }
}
