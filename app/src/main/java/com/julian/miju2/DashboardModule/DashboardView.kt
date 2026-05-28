package com.julian.miju2.DashboardModule

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.julian.miju2.R
import com.julian.miju2.presentation.components.BottomTab
import com.julian.miju2.presentation.components.MijuBottomBar
import com.julian.miju2.presentation.model.TransactionUi
import com.julian.miju2.ui.theme.Background
import com.julian.miju2.ui.theme.OnSurfaceVariant
import com.julian.miju2.ui.theme.Primary
import com.julian.miju2.ui.theme.PrimaryDark
import com.julian.miju2.ui.theme.Secondary

@Composable
fun DashboardScreen(
    navController: NavController,
    documentId: String,
    viewModel: DashboardViewModel = viewModel()
) {
    LaunchedEffect(documentId) {
        viewModel.loadUserData(documentId)
        viewModel.loadTransactions(documentId)
    }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            MijuBottomBar(
                selectedTab = BottomTab.HOME,
                onHomeClick = {
                    // Ya estamos en Home; el ViewModel conserva los datos cargados, no es necesario recargar.
                },
                onTransactionsClick = {
                    navController.navigate("transactions/$documentId") {
                        launchSingleTop = true
                    }
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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
                    Text(
                        text = stringResource(id = R.string.app_name),
                        color = Primary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                IconButton(onClick = {
                    // TODO: navegar a la pantalla de notificaciones cuando esté implementada
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = stringResource(id = R.string.dashboard_notifications),
                        tint = Primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.dashboard_welcome_back),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = OnSurfaceVariant,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Primary
                    )
                } else {
                    val greeting = if (viewModel.fullName.isBlank()) {
                        stringResource(id = R.string.dashboard_greeting_generic)
                    } else {
                        stringResource(id = R.string.dashboard_greeting, viewModel.fullName)
                    }
                    Text(
                        text = greeting,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            BalanceCard(
                formattedBalance = viewModel.formattedBalance,
                accountNumber = documentId
            )

            Spacer(modifier = Modifier.height(24.dp))

            SendMoneyButton(onClick = {
                navController.navigate("send-money/$documentId")
            })

            Spacer(modifier = Modifier.height(32.dp))

            RecentActivitySection(
                items = viewModel.transactionsUi,
                onSeeAll = {
                    navController.navigate("transactions/$documentId") {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
internal fun TransactionItem(item: TransactionUi, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Primary.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = stringResource(id = R.string.dashboard_tx_icon_desc),
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column {
                val title = if (item.isIncoming) {
                    stringResource(id = R.string.dashboard_tx_received_from, item.counterparty)
                } else {
                    stringResource(id = R.string.dashboard_tx_sent_to, item.counterparty)
                }
                Text(
                    text = title,
                    color = Primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.subtitle,
                    color = OnSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }
        Text(
            text = item.amountText,
            color = if (item.isIncoming) Secondary else Primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RecentActivitySection(
    items: List<TransactionUi>,
    onSeeAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.dashboard_recent_activity),
                color = Primary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onSeeAll) {
                Text(
                    text = stringResource(id = R.string.dashboard_see_all),
                    color = Secondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        items.take(4).forEach { tx ->
            TransactionItem(item = tx)
        }
    }
}

@Composable
private fun SendMoneyButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Secondary.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = stringResource(id = R.string.dashboard_send_money),
                    tint = Secondary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(id = R.string.dashboard_send_money),
                color = Primary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun BalanceCard(
    formattedBalance: String,
    accountNumber: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(colors = listOf(PrimaryDark, Primary)))
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.dashboard_balance_label),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formattedBalance,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.dashboard_account_number, accountNumber),
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(id = R.string.dashboard_membership),
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
