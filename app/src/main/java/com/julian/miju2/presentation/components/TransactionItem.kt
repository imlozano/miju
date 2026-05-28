package com.julian.miju2.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.julian.miju2.R
import com.julian.miju2.presentation.model.TransactionUi
import com.julian.miju2.ui.theme.OnSurfaceVariant
import com.julian.miju2.ui.theme.Primary
import com.julian.miju2.ui.theme.Secondary

@Composable
fun TransactionItem(item: TransactionUi, modifier: Modifier = Modifier) {
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
