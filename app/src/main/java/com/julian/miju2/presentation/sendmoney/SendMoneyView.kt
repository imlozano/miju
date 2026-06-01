package com.julian.miju2.presentation.sendmoney

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.julian.miju2.R
import com.julian.miju2.presentation.components.MijuTextField
import com.julian.miju2.presentation.components.ShowMessageAlertDialog
import com.julian.miju2.ui.theme.Background
import com.julian.miju2.ui.theme.Error
import com.julian.miju2.ui.theme.OnSurface
import com.julian.miju2.ui.theme.OnSurfaceVariant
import com.julian.miju2.ui.theme.Primary
import com.julian.miju2.ui.theme.Secondary

@Composable
fun SendMoneyScreen(
    navController: NavController,
    documentId: String,
    viewModel: SendMoneyViewModel = viewModel()
) {
    LaunchedEffect(documentId) {
        viewModel.loadSenderBalance(documentId)
    }

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.sendSuccess) {
        if (viewModel.sendSuccess) {
            showSuccessDialog = true
        }
    }

    Scaffold(containerColor = Background) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
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
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.send_money_back),
                        tint = Primary
                    )
                }
                Text(
                    text = stringResource(id = R.string.send_money_title),
                    color = Primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.width(48.dp))
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.send_money_eyebrow),
                color = Secondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Text(
                text = stringResource(id = R.string.send_money_headline),
                color = Primary,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.send_money_available_balance, viewModel.formattedBalance),
                color = OnSurfaceVariant,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(id = R.string.send_money_label_recipient),
                color = OnSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            MijuTextField(
                label = "",
                value = viewModel.recipientDocument,
                onValueChange = { viewModel.onRecipientChange(it) },
                placeholder = stringResource(id = R.string.send_money_placeholder_recipient),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Text(
                text = stringResource(id = R.string.send_money_hint_recipient),
                color = OnSurfaceVariant,
                fontSize = 11.sp
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.send_money_label_amount),
                color = OnSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            MijuTextField(
                label = "",
                value = viewModel.amount,
                onValueChange = { viewModel.onAmountChange(it) },
                placeholder = "0",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.send_money_label_concept),
                color = OnSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            MijuTextField(
                label = "",
                value = viewModel.concept,
                onValueChange = { viewModel.onConceptChange(it) },
                placeholder = stringResource(id = R.string.send_money_placeholder_concept)
            )

            Spacer(Modifier.height(32.dp))

            viewModel.errorMessage?.let { errorRes ->
                Text(
                    text = stringResource(id = errorRes),
                    color = Error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Button(
                onClick = {
                    viewModel.validateAndResolve(documentId) {
                        showConfirmDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = !viewModel.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text(
                    text = stringResource(id = R.string.send_money_continue),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(24.dp))
        }

        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = {
                    Text(
                        text = stringResource(id = R.string.send_money_confirm_title),
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        ConfirmRow(
                            label = stringResource(id = R.string.send_money_confirm_to),
                            value = viewModel.recipientName.ifBlank { viewModel.recipientDocument }
                        )
                        Spacer(Modifier.height(8.dp))
                        ConfirmRow(
                            label = stringResource(id = R.string.send_money_confirm_amount),
                            value = viewModel.formattedAmount
                        )
                        Spacer(Modifier.height(8.dp))
                        ConfirmRow(
                            label = stringResource(id = R.string.send_money_confirm_concept),
                            value = viewModel.concept.ifBlank { stringResource(id = R.string.send_money_no_concept) }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showConfirmDialog = false
                            viewModel.sendMoney(documentId)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text(
                            text = stringResource(id = R.string.send_money_confirm_send),
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text(
                            text = stringResource(id = R.string.send_money_confirm_cancel),
                            color = OnSurfaceVariant
                        )
                    }
                },
                containerColor = Background
            )
        }

        if (showSuccessDialog) {
            ShowMessageAlertDialog(
                onConfirmation = {
                    showSuccessDialog = false
                    navController.navigate("dashboard/$documentId") {
                        popUpTo("dashboard/$documentId") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                dialogTitle = R.string.success_title,
                dialogText = R.string.send_money_success
            )
        }
    }
}

@Composable
private fun ConfirmRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = OnSurfaceVariant, fontSize = 14.sp)
        Text(text = value, color = Primary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
