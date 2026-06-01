package com.julian.miju2.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.julian.miju2.R

enum class BottomTab { HOME, TRANSACTIONS, PROFILE }

@Composable
fun MijuBottomBar(
    selectedTab: BottomTab,
    onHomeClick: () -> Unit,
    onTransactionsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val itemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == BottomTab.HOME,
            onClick = onHomeClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null
                )
            },
            label = { Text(text = stringResource(id = R.string.nav_home)) },
            colors = itemColors
        )
        NavigationBarItem(
            selected = selectedTab == BottomTab.TRANSACTIONS,
            onClick = onTransactionsClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = null
                )
            },
            label = { Text(text = stringResource(id = R.string.nav_transactions)) },
            colors = itemColors
        )
        NavigationBarItem(
            selected = selectedTab == BottomTab.PROFILE,
            onClick = onProfileClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
            },
            label = { Text(text = stringResource(id = R.string.nav_profile)) },
            colors = itemColors
        )
    }
}
