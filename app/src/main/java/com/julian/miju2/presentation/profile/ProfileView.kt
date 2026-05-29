package com.julian.miju2.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.julian.miju2.R
import com.julian.miju2.presentation.components.BottomTab
import com.julian.miju2.presentation.components.MijuBottomBar
import com.julian.miju2.presentation.components.ShowLoadingAlertDialog
import com.julian.miju2.presentation.components.ShowMessageAlertDialog
import com.julian.miju2.ui.theme.*

@Composable
fun ProfileInfoField(
    label: String,
    value: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurfaceVariant.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Neutral,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = value,
                modifier = Modifier.padding(16.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )
        }
    }
}

@Composable
fun ProfileActionItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = Neutral,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = Primary.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                modifier = Modifier.weight(1.0f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = OnSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun ProfileScreen(
    navController: NavController,
    documentId: String,
    viewModel: ProfileViewModel = viewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableIntStateOf(R.string.error_title) }
    var dialogMessage by remember { mutableIntStateOf(0) }

    LaunchedEffect(documentId) {
        viewModel.loadUserData(documentId)
    }

    if (viewModel.isLoading) {
        ShowLoadingAlertDialog()
    }

    if (showResultDialog) {
        ShowMessageAlertDialog(
            onConfirmation = { showResultDialog = false },
            dialogTitle = dialogTitle,
            dialogText = dialogMessage
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                viewModel.resetPasswordState()
            },
            title = {
                Text(
                    text = stringResource(id = R.string.profile_btn_change_password),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = stringResource(id = R.string.profile_change_password_instruction),
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = viewModel.newPassword,
                        onValueChange = { viewModel.onNewPasswordChange(it) },
                        label = { Text(text = stringResource(id = R.string.profile_label_new_password)) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = viewModel.passwordError != null,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    if (viewModel.passwordError != null) {
                        Text(
                            text = stringResource(id = viewModel.passwordError!!),
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = viewModel.confirmNewPassword,
                        onValueChange = { viewModel.onConfirmNewPasswordChange(it) },
                        label = { Text(text = stringResource(id = R.string.signup_label_confirm_password)) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = viewModel.confirmPasswordError != null,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    if (viewModel.confirmPasswordError != null) {
                        Text(
                            text = stringResource(id = viewModel.confirmPasswordError!!),
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onChangePasswordClick { success, messageResId ->
                            if (success) {
                                showDialog = false
                                dialogTitle = R.string.success_title
                                dialogMessage = R.string.profile_password_update_success
                            } else {
                                dialogTitle = R.string.error_title
                                dialogMessage = messageResId
                            }
                            showResultDialog = true
                        }
                    },
                    enabled = viewModel.newPassword.isNotEmpty() &&
                            viewModel.confirmNewPassword.isNotEmpty() &&
                            !viewModel.isChangingPassword
                ) {
                    if (viewModel.isChangingPassword) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(text = stringResource(id = R.string.btn_save))
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        viewModel.resetPasswordState()
                    }
                ) {
                    Text(text = stringResource(id = R.string.btn_cancel))
                }
            }
        )
    }

    if (viewModel.navigateToLogin) {
        LaunchedEffect(Unit) {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
            viewModel.onNavigationHandled()
        }
    }

    Scaffold(
        bottomBar = {
            MijuBottomBar(
                selectedTab = BottomTab.PROFILE,
                onHomeClick = { navController.navigate("dashboard/$documentId") },
                onTransactionsClick = { navController.navigate("transactions/$documentId") },
                onProfileClick = { }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = Neutral
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        color = Primary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    IconButton(onClick = { /* Notifications */ }) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = null,
                            tint = Primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // User Info
                Text(
                    text = viewModel.fullName,
                    color = Primary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = viewModel.email,
                    color = OnSurfaceVariant,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Personal Data Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Background),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PersonOutline,
                                contentDescription = null,
                                tint = Primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(id = R.string.profile_title_personal_data),
                                color = Primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        ProfileInfoField(
                            label = stringResource(id = R.string.signup_label_fullname),
                            value = viewModel.fullName
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ProfileInfoField(
                            label = stringResource(id = R.string.profile_label_email),
                            value = viewModel.email
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ProfileInfoField(
                            label = stringResource(id = R.string.signup_label_cellphone_number),
                            value = viewModel.cellphoneNumber
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Security Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Background),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(id = R.string.profile_title_security),
                                color = Primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        ProfileActionItem(
                            icon = Icons.Default.History,
                            text = stringResource(id = R.string.profile_btn_change_password),
                            onClick = { showDialog = true }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Logout Button
                OutlinedButton(
                    onClick = { viewModel.onLogoutClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, ErrorLight),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Error)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.profile_btn_logout),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = stringResource(id = R.string.profile_footer_version),
                    color = OnSurfaceVariant.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
