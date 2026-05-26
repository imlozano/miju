package com.julian.miju2.TransactionsModule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.julian.miju2.DashboardModule.TransactionItem
import com.julian.miju2.R
import com.julian.miju2.components.BottomTab
import com.julian.miju2.components.MijuBottomBar
import com.julian.miju2.ui.theme.Background
import com.julian.miju2.ui.theme.OnSurfaceVariant
import com.julian.miju2.ui.theme.Primary

@Composable
fun TransactionsScreen(
    navController: NavController,
    documentId: String,
    viewModel: TransactionsViewModel = viewModel()
) {
    LaunchedEffect(documentId) {
        viewModel.loadTransactions(documentId)
    }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            MijuBottomBar(
                selectedTab = BottomTab.TRANSACTIONS,
                onHomeClick = {
                    navController.navigate("dashboard/$documentId") {
                        launchSingleTop = true
                    }
                },
                onTransactionsClick = {
                    // Ya estamos en Transactions; no recargar.
                },
                onProfileClick = {
                    navController.navigate("profile/$documentId") {
                        launchSingleTop = true
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.transactions_back),
                        tint = Primary
                    )
                }
                Text(
                    text = stringResource(R.string.transactions_title),
                    color = Primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = viewModel.initial,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (viewModel.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (viewModel.transactionsUi.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.transactions_empty),
                        color = OnSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    items(
                        items = viewModel.transactionsUi,
                        key = { it.transactionId }
                    ) { tx ->
                        TransactionItem(item = tx)
                    }
                }
            }
        }
    }
}
