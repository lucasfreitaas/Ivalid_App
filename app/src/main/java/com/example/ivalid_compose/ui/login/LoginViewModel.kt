package com.example.ivalid_compose.ui.login


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.regex.Pattern
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.delay
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

    private val emailRegex = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    )

    fun onEmailChange(new: String) {
        uiState = uiState.copy(
            email = new,
            emailError = null
        )
        updateLoginEnabled()
    }

    fun onPasswordChange(new: String) {
        uiState = uiState.copy(
            password = new,
            passwordError = null
        )
        updateLoginEnabled()
    }

    fun togglePasswordVisibility() {
        uiState = uiState.copy(isPasswordVisible = !uiState.isPasswordVisible)
    }

    private fun updateLoginEnabled() {
        val isValid = uiState.email.isNotBlank() && uiState.password.length >= 6
        uiState = uiState.copy(isLoginEnabled = isValid)
    }

    fun validate(): Boolean {
        var emailError: String? = null
        var passwordError: String? = null

        if (!emailRegex.matcher(uiState.email).matches()) {
            emailError = "E-mail inválido"
        }
        if (uiState.password.length < 6) {
            passwordError = "Mínimo 6 caracteres"
        }

        uiState = uiState.copy(emailError = emailError, passwordError = passwordError)
        return emailError == null && passwordError == null
    }


    fun login() {
        if (!validate()){
            return
        }

        uiState = uiState.copy(isLoading = true, authError = null)

        val email = uiState.email.trim()
        val password = uiState.password.trim()
        val auth = FirebaseAuth.getInstance()

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                uiState = uiState.copy(isLoading = false)
                viewModelScope.launch {
                    _navigationEvent.emit(Unit)
                }
            }
            .addOnFailureListener { e ->
                uiState = uiState.copy(isLoading = false)

                val msg = when(e){
                    is FirebaseAuthException -> when(e.errorCode){
                        "ERROR_INVALID_CREDENTIAL" -> "E-mail ou senha incorretos."
                        else -> "Erro ao entrar: ${e.errorCode}. Tente novamente!"
                    }
                    else -> "Erro desconhecido ao tentar entrar. Tente novamente."
                }

                uiState = uiState.copy(authError = msg)
            }
    }
}