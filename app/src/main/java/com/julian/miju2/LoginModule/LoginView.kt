package com.julian.miju2.LoginModule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.julian.miju2.ui.theme.*

@Composable
fun LoginScreen(viewModel: LoginViewModel = viewModel()) {
    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "MiJu",
                color = Primary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Bienvenido de nuevo",
                color = OnSurface,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Ingresa tus credenciales para acceder a tu panel.",
                color = OnSurfaceVariant,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Correo Electrónico",
                color = OnSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = viewModel.email,
                onValueChange = { nuevoEmail -> viewModel.onEmailChange(nuevoEmail) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("tu@ejemplo.com", color = OnSurfaceVariant) },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = OnSurfaceVariant)
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Neutral,
                    unfocusedContainerColor = Neutral,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Contraseña",
                    color = OnSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    color = Secondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = viewModel.password,
                onValueChange = { nuevoPassword -> viewModel.onPasswordChange(nuevoPassword) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("••••••••", color = OnSurfaceVariant) },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = OnSurfaceVariant)
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Neutral,
                    unfocusedContainerColor = Neutral,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                visualTransformation = if (viewModel.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(
                        onClick = { viewModel.togglePasswordVisibility() }
                    ) {
                        Icon(
                            imageVector = if (viewModel.passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (viewModel.passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = OnSurfaceVariant
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = viewModel.rememberMe,
                    onCheckedChange = { nuevoValor -> viewModel.onRememberChange(nuevoValor) }
                )
                Text(
                    text = "Recordar sesión en este dispositivo",
                    color = OnSurfaceVariant,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Ingresar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "¿No tienes una cuenta? ",
                    color = OnSurfaceVariant,
                    fontSize = 14.sp
                )
                Text(
                    text = "Regístrate ahora",
                    color = Primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginPreview() {
    Miju2Theme {
        LoginScreen()
    }
}