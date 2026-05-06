package com.julian.miju2.SignUpModule

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.julian.miju2.ui.theme.*
import com.julian.miju2.R

@Composable
fun SignUpTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    error: String? = null
) {
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder,
                color = OnSurfaceVariant.copy(alpha = 0.5f), fontSize = 14.sp) },
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Neutral,
                unfocusedContainerColor = Neutral,
                disabledContainerColor = Neutral,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(8.dp),
            isError = error != null,
            supportingText = {
                if (error != null) {
                    Text(text = error, color = MaterialTheme.colorScheme.error, fontSize = 10.sp)
                }
            }
        )
    }
}

@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpViewModel = viewModel()
) {
    // Escuchar el estado de éxito para navegar
    LaunchedEffect(viewModel.signUpSuccess) {
        if (viewModel.signUpSuccess) {
            navController.navigate("login") {
                // Limpiar el historial para que no pueda volver al registro dándole atrás
                popUpTo("signup") { inclusive = true }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Neutral
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
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
                Text(
                    text = stringResource(id = R.string.signup_tittle_secure),
                    color = OnSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(id = R.string.signup_subtitle_step),
                color = OnSurfaceVariant,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Background),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    SignUpTextField(
                        label = stringResource(id = R.string.signup_label_fullname),
                        value = viewModel.fullName,
                        onValueChange = { viewModel.onFullNameChange(it) },
                        placeholder = stringResource(id = R.string.signup_placeholder_fullname),
                        error = viewModel.fullNameError
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                    SignUpTextField(
                        label = stringResource(id = R.string.signup_label_document),
                        value = viewModel.documentId,
                        onValueChange = { viewModel.onDocumentIdChange(it) },
                        placeholder = stringResource(id = R.string.signup_placeholder_document),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        error = viewModel.documentIdError
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SignUpTextField(
                        label = stringResource(id = R.string.signup_label_email),
                        value = viewModel.email,
                        onValueChange = { viewModel.onEmailChange(it) },
                        placeholder = stringResource(id = R.string.signup_placeholder_email),
                        error = viewModel.emailError
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SignUpTextField(
                        label = stringResource(id = R.string.signup_label_password),
                        value = viewModel.password,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        placeholder = stringResource(id = R.string.signup_placeholder_password),
                        isPassword = true,
                        error = viewModel.passwordError
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SignUpTextField(
                        label = stringResource(id = R.string.signup_label_confirm_password),
                        value = viewModel.confirmPassword,
                        onValueChange = { viewModel.onConfirmPasswordChange(it) },
                        placeholder = stringResource(id = R.string.signup_placeholder_password),
                        isPassword = true,
                        error = viewModel.confirmPasswordError
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.Top) {
                        Checkbox(
                            checked = viewModel.acceptedTerms,
                            onCheckedChange = { viewModel.onTermsChange(it) },
                            colors = CheckboxDefaults.colors(checkedColor = Primary)
                        )
                        Text(
                            text = buildAnnotatedString {
                                append(stringResource(id = R.string.signup_terms_accept))
                                withStyle(style = SpanStyle(color = Primary, fontWeight = FontWeight.Bold)) {
                                    append(stringResource(id = R.string.signup_terms_service))
                                }
                                append(stringResource(id = R.string.signup_terms_and))
                                withStyle(style = SpanStyle(color = Primary, fontWeight = FontWeight.Bold)) {
                                    append(stringResource(id = R.string.signup_terms_privacy))
                                }
                                append(stringResource(id = R.string.signup_terms_process))
                            },
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = OnSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.onSignUpClick() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(color = Background,
                                modifier = Modifier.size(24.dp))
                        } else {
                            Text(stringResource(id = R.string.signup_btn_create_account),
                                fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ID Verification Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Neutral),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(id = R.string.signup_id_verification_tittle),
                        color = Primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.signup_id_verification_label_instruction),
                        color = OnSurfaceVariant,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        color = Color.Transparent,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, OnSurfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(
                                    modifier = Modifier.size(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Primary.copy(alpha = 0.1f)
                                ) {
                                    IconButton(onClick = { viewModel.onOpenCamera() }) {
                                        Icon(
                                            Icons.Default.CameraAlt,
                                            contentDescription = null,
                                            tint = Primary
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    stringResource(id = R.string.signup_btn_open_camera),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary,
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
                    withStyle(style = SpanStyle(color = Secondary, fontWeight = FontWeight.Bold)) {
                        append(stringResource(id = R.string.signup_login_link))
                    }
                },
                modifier = Modifier.fillMaxWidth().clickable {
                   navController.navigate("login")
                },
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = OnSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
