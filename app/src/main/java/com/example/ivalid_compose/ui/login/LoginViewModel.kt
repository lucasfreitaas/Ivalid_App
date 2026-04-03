package com.example.ivalid_compose.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val isLoginEnabled: Boolean = false,
    val authError: String? = null
)

class LoginViewModel : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    // Regex Kotlin nativa — mais leve que java.util.regex.Pattern
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

    fun onEmailChange(new: String) {
        uiState = uiState.copy(email = new, emailError = null)
        updateLoginEnabled()
    }

    fun onPasswordChange(new: String) {
        uiState = uiState.copy(password = new, passwordError = null)
        updateLoginEnabled()
    }

    fun togglePasswordVisibility() {
        uiState = uiState.copy(isPasswordVisible = !uiState.isPasswordVisible)
    }

    private fun updateLoginEnabled() {
        uiState = uiState.copy(isLoginEnabled = uiState.email.isNotBlank() && uiState.password.length >= 6)
    }

    fun validate(): Boolean {
        val emailError = if (!emailRegex.matches(uiState.email)) "E-mail inválido" else null
        val passwordError = if (uiState.password.length < 6) "Mínimo 6 caracteres" else null
        uiState = uiState.copy(emailError = emailError, passwordError = passwordError)
        return emailError == null && passwordError == null
    }

    fun login() {
        if (!validate()) return

        uiState = uiState.copy(isLoading = true, authError = null)

        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(uiState.email.trim(), uiState.password.trim())
            .addOnSuccessListener {
                uiState = uiState.copy(isLoading = false)
                viewModelScope.launch { _navigationEvent.emit(Unit) }
            }
            .addOnFailureListener { e ->
                val msg = when {
                    e is FirebaseAuthException && e.errorCode == "ERROR_INVALID_CREDENTIAL" ->
                        "E-mail ou senha incorretos."
                    e is FirebaseAuthException ->
                        "Erro ao entrar: ${e.errorCode}. Tente novamente!"
                    else -> "Erro desconhecido ao tentar entrar. Tente novamente."
                }
                uiState = uiState.copy(isLoading = false, authError = msg)
            }
    }
}