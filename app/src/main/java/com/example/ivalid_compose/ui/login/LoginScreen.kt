package com.example.ivalid_compose.ui.login

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ivalid_compose.R
import com.example.ivalid_compose.ui.theme.AppTheme
import com.example.ivalid_compose.ui.theme.GreenAccent
import com.example.ivalid_compose.ui.theme.RedPrimary
import com.example.ivalid_compose.ui.theme.RedPrimaryDark

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onForgotPassword: () -> Unit,
    onSignUp: () -> Unit,
    navController: androidx.navigation.NavController
) {
    val state = viewModel.uiState
    val focus = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect {
            navController.navigate("home"){
                popUpTo(navController.graph.id){
                    inclusive = true
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(36.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_ivalid),
            contentDescription = "Logo Ivalid",
            modifier = Modifier
                .size(190.dp)
                .padding(top = 8.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Bem-vindo de volta",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Box(
            Modifier
                .height(4.dp)
                .width(64.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(GreenAccent.copy(alpha = 0.9f))
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Entre com sua conta Ivalid",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
        )

        Spacer(Modifier.height(28.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            shadowElevation = 0.dp
        ) {
            Column(Modifier.padding(20.dp)) {

                OutlinedTextField(
                    value = state.email,
                    onValueChange = viewModel::onEmailChange,
                    label = { Text("E-mail") },
                    leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                    isError = state.emailError != null,
                    supportingText = {
                        AnimatedVisibility(state.emailError != null) {
                            Text(
                                state.emailError.orEmpty(),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { /* foca no próximo campo */ }
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                        disabledIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        focusedLeadingIconColor = GreenAccent,
                        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.password,
                    onValueChange = viewModel::onPasswordChange,
                    label = { Text("Senha") },
                    leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                            Icon(
                                imageVector = if (state.isPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                contentDescription = if (state.isPasswordVisible) "Ocultar senha" else "Mostrar senha",
                                tint = GreenAccent
                            )
                        }
                    },
                    isError = state.passwordError != null,
                    supportingText = {
                        AnimatedVisibility(state.passwordError != null) {
                            Text(
                                state.passwordError.orEmpty(),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    singleLine = true,
                    visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focus.clearFocus()
                            if (state.isLoginEnabled && !state.isLoading){
                                viewModel.login()
                            }
                        }
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                        disabledIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        focusedLeadingIconColor = GreenAccent,
                        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                AnimatedVisibility(visible = state.authError != null) {
                    Text(
                        text = state.authError.orEmpty(),
                        color = RedPrimary,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onForgotPassword) {
                        Text("Esqueci minha senha", color = GreenAccent)
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))


        GradientRedButton(
            text = if (state.isLoading) "Entrando..." else "Entrar",
            enabled = state.isLoginEnabled && !state.isLoading,
            onClick = {
                focus.clearFocus()
                viewModel.login()
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(14.dp))


        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Não tem conta?",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Cadastre-se",
                color = GreenAccent,
                modifier = Modifier.clickable { onSignUp() }
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun GradientRedButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(18.dp)
    val gradient = Brush.horizontalGradient(
        colors = listOf(RedPrimary, RedPrimaryDark)
    )
    val alpha = if (enabled) 1f else 0.5f

    Box(
        modifier = modifier
            .height(54.dp)
            .clip(shape)
            .background(brush = gradient, alpha = alpha)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Color.White
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "Light")
@Composable
private fun PreviewLoginLight() {
    AppTheme(darkTheme = false) {
        Surface {
            LoginScreen(
                viewModel = LoginViewModel(),
                onForgotPassword = {},
                onSignUp = {},
                navController = rememberNavController(),
            )
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "Dark")
@Composable
private fun PreviewLoginDark() {
    AppTheme(darkTheme = true) {
        Surface {
            LoginScreen(
                viewModel = LoginViewModel(),
                onForgotPassword = {},
                onSignUp = {},
                navController = rememberNavController(),
            )
        }
    }
}