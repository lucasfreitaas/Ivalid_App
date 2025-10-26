package com.example.ivalid_compose.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class UserProfile(
    val name: String = "Usuário Ivalid",
    val email: String = "email@nao-logado.com",
    val phone: String? = null,
    val isVerified: Boolean = false,
    val uid: String = "",
)

data class ProfileUiState(
    val userProfile: UserProfile = UserProfile(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProfileViewModel : ViewModel() {
    var uiState by mutableStateOf(ProfileUiState())
        private set

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        val user = FirebaseAuth.getInstance().currentUser
        val firestore = FirebaseFirestore.getInstance()

        if (user == null) {
            uiState = uiState.copy(
                userProfile = UserProfile(email = "Nenhum usuário logado."),
                isLoading = false
            )
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try{
                val userDocument = firestore
                    .collection("users")
                    .document(user.uid)
                    .get()
                    .await()

                val firestoreName = userDocument.getString("fullName")
                val finalName = firestoreName ?: user.displayName ?: "Cliente Ivalid"

                val profile = UserProfile(
                    name = finalName,
                    email = user.email ?: "Email indisponível",
                    phone = user.phoneNumber,
                    isVerified = user.isEmailVerified,
                    uid = user.uid
                )

                uiState = uiState.copy(
                    userProfile = profile,
                    isLoading = false
                )
            } catch (e: Exception){
                uiState = uiState.copy(
                    error = "Erro ao carregar perfil: ${e.localizedMessage}",
                    isLoading = false
                )
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                FirebaseAuth.getInstance().signOut()
                onSuccess()
            } catch (e: Exception) {
                uiState = uiState.copy(error = "Erro ao fazer logout.")
            }
        }
    }
}