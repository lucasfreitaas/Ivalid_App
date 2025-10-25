package com.example.ivalid_compose.ui.signup

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
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.ivalid_compose.R
import com.example.ivalid_compose.ui.theme.AppTheme
import com.example.ivalid_compose.ui.theme.GreenAccent
import com.example.ivalid_compose.ui.theme.RedPrimary
import com.example.ivalid_compose.ui.theme.RedPrimaryDark

import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.withStyle




@Composable
private fun ClickableTermsText(
    onTerms: () -> Unit,
    onPrivacy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val baseColor = MaterialTheme.colorScheme.onSurface
    val linkColor = GreenAccent

    val annotated = buildAnnotatedString {
        append("Li e aceito os ")

        pushStringAnnotation(tag = "TERMS", annotation = "terms")
        withStyle(
            SpanStyle(
                color = linkColor,
                fontWeight = FontWeight.SemiBold,
                textDecoration = TextDecoration.Underline
            )
        ) { append("Termos de Uso") }
        pop()

        append(" e a ")

        pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
        withStyle(
            SpanStyle(
                color = linkColor,
                fontWeight = FontWeight.SemiBold,
                textDecoration = TextDecoration.Underline
            )
        ) { append("Política de Privacidade") }
        pop()
    }

    ClickableText(
        text = annotated,
        style = MaterialTheme.typography.bodyMedium.copy(color = baseColor),
        modifier = modifier
    ) { offset ->
        annotated.getStringAnnotations(start = offset, end = offset)
            .firstOrNull()?.let { ann ->
                when (ann.tag) {
                    "TERMS" -> onTerms()
                    "PRIVACY" -> onPrivacy()
                }
            }
    }
}




@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel,
    onBackToLogin: () -> Unit,
    onShowTerms: () -> Unit,
    onAccountCreated: () -> Unit
) {
    val state = viewModel.uiState
    val focus = LocalFocusManager.current

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
                .size(96.dp)
                .padding(top = 8.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Crie sua conta",
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
            text = "Preencha seus dados para começar",
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
                    value = state.fullName,
                    onValueChange = viewModel::onFullNameChange,
                    label = { Text("Nome completo") },
                    leadingIcon = { Icon(Icons.Outlined.Person, null) },
                    isError = state.fullNameError != null,
                    supportingText = {
                        AnimatedVisibility(state.fullNameError != null) {
                            Text(state.fullNameError.orEmpty(), color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
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
                    value = state.email,
                    onValueChange = viewModel::onEmailChange,
                    label = { Text("E-mail") },
                    leadingIcon = { Icon(Icons.Outlined.Email, null) },
                    isError = state.emailError != null,
                    supportingText = {
                        AnimatedVisibility(state.emailError != null) {
                            Text(state.emailError.orEmpty(), color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
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
                    leadingIcon = { Icon(Icons.Outlined.Lock, null) },
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
                            Text(state.passwordError.orEmpty(), color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
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
                    value = state.confirmPassword,
                    onValueChange = viewModel::onConfirmPasswordChange,
                    label = { Text("Confirmar senha") },
                    leadingIcon = { Icon(Icons.Outlined.Lock, null) },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.toggleConfirmPasswordVisibility() }) {
                            Icon(
                                imageVector = if (state.isConfirmPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                contentDescription = if (state.isConfirmPasswordVisible) "Ocultar senha" else "Mostrar senha",
                                tint = GreenAccent
                            )
                        }
                    },
                    isError = state.confirmPasswordError != null,
                    supportingText = {
                        AnimatedVisibility(state.confirmPasswordError != null) {
                            Text(state.confirmPasswordError.orEmpty(), color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    visualTransformation = if (state.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (!state.isLoading && state.isSignUpEnabled) {
                                viewModel.signUp(
                                    onSuccess = onAccountCreated,
                                    onError = { /* TODO: snackbar/toast */ }
                                )
                            }
                        }
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        focusedLeadingIconColor = GreenAccent,
                        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))


                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = state.acceptTerms,
                        onCheckedChange = { viewModel.onAcceptTermsChange(it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = MaterialTheme.colorScheme.outline,
                            checkmarkColor = Color.White
                        ),
                        modifier = Modifier.padding(top = 2.dp)
                    )


                    ClickableTermsText(
                        onTerms = { onShowTerms() },
                        onPrivacy = { onShowTerms() },
                        modifier = Modifier
                            .padding(start = 8.dp, top = 6.dp)
                            .weight(1f)
                    )
                }


                AnimatedVisibility(visible = state.termsError != null) {
                    Text(
                        text = state.termsError.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 6.dp, start = 4.dp)
                    )
                }


                AnimatedVisibility(visible = state.termsError != null) {
                    Text(
                        text = state.termsError.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))


        GradientRedButton(
            text = if (state.isLoading) "Criando..." else "Criar conta",
            enabled = state.isSignUpEnabled && !state.isLoading,
            onClick = {
                viewModel.signUp(
                    onSuccess = onAccountCreated,
                    onError = { /* TODO: snackbar/toast */ }
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(14.dp))


        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Já tem uma conta?",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Entrar",
                color = GreenAccent,
                modifier = Modifier.clickable { onBackToLogin() }
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
@Preview(showBackground = true, name = "SignUp - Light")
@Composable
private fun PreviewSignUpLight() {
    AppTheme(darkTheme = false) {
        Surface {
            SignUpScreen(
                viewModel = SignUpViewModel(),
                onBackToLogin = {},
                onShowTerms = {},
                onAccountCreated = {}
            )
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "SignUp - Dark")
@Composable
private fun PreviewSignUpDark() {
    AppTheme(darkTheme = true) {
        Surface {
            SignUpScreen(
                viewModel = SignUpViewModel(),
                onBackToLogin = {},
                onShowTerms = {},
                onAccountCreated = {}
            )
        }
    }
}
