package com.julian.miju2.presentation.signUp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.julian.miju2.R
import com.julian.miju2.presentation.components.ShowLoadingAlertDialog
import com.julian.miju2.presentation.components.ShowMessageAlertDialog
import com.julian.miju2.ui.theme.*
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.julian.miju2.presentation.components.MijuTextField



@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    var showResultDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableIntStateOf(R.string.error_title) }
    var dialogMessage by remember { mutableIntStateOf(0) }
    var isSuccess by remember { mutableStateOf(false) }

    // Recibe el texto crudo del OCR que la pantalla de cámara dejó en el backstack.
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val ocrText = savedStateHandle
        ?.getStateFlow<String?>("ocr_raw_text", null)
        ?.collectAsState()
    LaunchedEffect(ocrText?.value) {
        ocrText?.value?.let { rawText ->
            viewModel.applyOcrText(rawText)
            savedStateHandle.remove<String>("ocr_raw_text")
        }
    }

    if (viewModel.isLoading) {
        ShowLoadingAlertDialog()
    }

    if (showResultDialog) {
        ShowMessageAlertDialog(
            onConfirmation = {
                showResultDialog = false
                if (isSuccess) {
                    navController.navigate("login") {
                        popUpTo("signUp") { inclusive = true }
                    }
                }
            },
            dialogTitle = dialogTitle,
            dialogText = dialogMessage
        )
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surfaceVariant) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = stringResource(id = R.string.signup_tittle_secure),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = stringResource(
                id = R.string.signup_subtitle_step),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    MijuTextField(
                        label = stringResource(id = R.string.signup_label_fullname),
                        value = viewModel.fullName,
                        onValueChange = { viewModel.onFullNameChange(it) },
                        placeholder = stringResource(id = R.string.signup_placeholder_fullname),
                        error = viewModel.fullNameError
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    MijuTextField(
                        label = stringResource(id = R.string.signup_label_document),
                        value = viewModel.documentId,
                        onValueChange = { viewModel.onDocumentIdChange(it) },
                        placeholder = stringResource(id = R.string.signup_placeholder_document),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        error = viewModel.documentIdError
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    MijuTextField(
                        label = stringResource(id = R.string.signup_label_email),
                        value = viewModel.email,
                        onValueChange = { viewModel.onEmailChange(it) },
                        placeholder = stringResource(id = R.string.signup_placeholder_email),
                        error = viewModel.emailError
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    MijuTextField(
                        label = stringResource(id = R.string.signup_label_cellphone_number),
                        value = viewModel.cellphoneNumber,
                        onValueChange = { viewModel.onCellphoneNumberChange(it) },
                        placeholder = stringResource(id = R.string.signup_placeholder_cellphone_number),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        error = viewModel.cellphoneNumberError
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    MijuTextField(
                        label = stringResource(id = R.string.signup_label_password),
                        value = viewModel.password,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        placeholder = stringResource(id = R.string.signup_placeholder_password),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        isPassword = true,
                        visualTransformation = if (viewModel.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                                Icon(
                                    imageVector = if (viewModel.passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        error = viewModel.passwordError
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    MijuTextField(
                        label = stringResource(id = R.string.signup_label_confirm_password),
                        value = viewModel.confirmPassword,
                        onValueChange = { viewModel.onConfirmPasswordChange(it) },
                        placeholder = stringResource(id = R.string.signup_placeholder_password),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        isPassword = true,
                        visualTransformation = if (viewModel.confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { viewModel.toggleConfirmPasswordVisibility() }) {
                                Icon(
                                    imageVector = if (viewModel.confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        error = viewModel.confirmPasswordError
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.Top) {
                        Checkbox(
                            checked = viewModel.acceptedTerms,
                            onCheckedChange = { viewModel.onTermsChange(it) },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                        )
                        Text(
                            text = buildAnnotatedString {
                                append(stringResource(id = R.string.signup_terms_accept))

                                withStyle(style = SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold)
                                ) { append(stringResource(id = R.string.signup_terms_service)) }

                                append(stringResource(id = R.string.signup_terms_and))

                                withStyle(style = SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold))
                                { append(stringResource(id = R.string.signup_terms_privacy)) }

                                append(stringResource(id = R.string.signup_terms_process))
                            },
                            fontSize = 11.sp, lineHeight = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    viewModel.termsError?.let { errorRes ->
                        Text(
                            text = stringResource(id = errorRes),
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { 
                            viewModel.onSignUpClick { success, messageResId ->
                                isSuccess = success
                                dialogTitle = if (success) R.string.success_title else R.string.error_title
                                dialogMessage = messageResId
                                showResultDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        enabled = !viewModel.isLoading
                    ) {
                        Text(stringResource(id = R.string.signup_btn_create_account), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text(text = stringResource(
                        id = R.string.signup_id_verification_tittle),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.signup_id_verification_label_instruction),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        color = Color.Transparent,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                                Surface(
                                    modifier = Modifier.size(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)) {
                                    IconButton(onClick = { navController.navigate("camera-scanner") })
                                    { Icon(Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary) }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(stringResource(
                                    id = R.string.signup_btn_open_camera),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = buildAnnotatedString {
                    append(stringResource(id = R.string.signup_account_created))
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold))
                    { append(stringResource(id = R.string.signup_login_link)) }
                },
                modifier = Modifier.fillMaxWidth().clickable { navController.navigate("login") },
                textAlign = TextAlign.Center, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
