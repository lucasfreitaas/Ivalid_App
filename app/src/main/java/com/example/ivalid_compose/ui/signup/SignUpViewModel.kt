package com.example.ivalid_compose.ui.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.regex.Pattern

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


data class SignUpUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val acceptTerms: Boolean = false,

    val fullNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val termsError: String? = null,

    val isLoading: Boolean = false,
    val isSignUpEnabled: Boolean = false
)

class SignUpViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    var uiState by mutableStateOf(SignUpUiState())
        private set

    private val emailRegex = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

    fun onFullNameChange(new: String) {
        uiState = uiState.copy(fullName = new, fullNameError = null)
        updateEnabled()
    }

    fun onEmailChange(new: String) {
        uiState = uiState.copy(email = new, emailError = null)
        updateEnabled()
    }

    fun onPasswordChange(new: String) {
        uiState = uiState.copy(password = new, passwordError = null)
        updateEnabled()
    }

    fun onConfirmPasswordChange(new: String) {
        uiState = uiState.copy(confirmPassword = new, confirmPasswordError = null)
        updateEnabled()
    }

    fun togglePasswordVisibility() {
        uiState = uiState.copy(isPasswordVisible = !uiState.isPasswordVisible)
    }

    fun toggleConfirmPasswordVisibility() {
        uiState = uiState.copy(isConfirmPasswordVisible = !uiState.isConfirmPasswordVisible)
    }

    fun onAcceptTermsChange(new: Boolean) {
        uiState = uiState.copy(acceptTerms = new, termsError = null)
        updateEnabled()
    }

    private fun updateEnabled() {
        val validBase = uiState.fullName.isNotBlank() &&
                uiState.email.isNotBlank() &&
                uiState.password.length >= 6 &&
                uiState.confirmPassword.isNotBlank() &&
                uiState.acceptTerms
        uiState = uiState.copy(isSignUpEnabled = validBase)
    }

    private fun validate(): Boolean {
        var fullNameError: String? = null
        var emailError: String? = null
        var passwordError: String? = null
        var confirmPasswordError: String? = null
        var termsError: String? = null

        if (uiState.fullName.trim().length < 3) {
            fullNameError = "Informe seu nome completo"
        }
        if (!emailRegex.matcher(uiState.email).matches()) {
            emailError = "E-mail inválido"
        }
        if (uiState.password.length < 6) {
            passwordError = "Mínimo 6 caracteres"
        }
        if (uiState.confirmPassword != uiState.password) {
            confirmPasswordError = "As senhas não coincidem"
        }
        if (!uiState.acceptTerms) {
            termsError = "Você precisa aceitar os termos"
        }

        uiState = uiState.copy(
            fullNameError = fullNameError,
            emailError = emailError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError,
            termsError = termsError
        )
        return fullNameError == null &&
                emailError == null &&
                passwordError == null &&
                confirmPasswordError == null &&
                termsError == null
    }


    fun signUp(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (!validate()) return
       uiState = uiState.copy(isLoading = true)

        val email = uiState.email
        val password = uiState.password
        val fullName = uiState.fullName

        auth.createUserWithEmailAndPassword(uiState.email, uiState.password)
            .addOnCompleteListener {
                task -> if (task.isSuccessful){
                    onSuccess()
                } else {
                    onError(task.exception?.message ?: "Erro ao cadastrar usuário")
                    return@addOnCompleteListener
                }


                val uid = auth.currentUser!!.uid
                val data = mapOf(
                    "fullName" to fullName,
                    "email" to email,
                    "createdAt" to System.currentTimeMillis()
                )

                db.collection("users").document(uid)
                    .set(data)
                    .addOnSuccessListener {
                        uiState = uiState.copy(isLoading = false)
                        onSuccess
                    }
                    .addOnFailureListener {
                        uiState = uiState.copy(isLoading = false)
                        onError("Erro ao salvar dados")
                    }
            }
    }

}
