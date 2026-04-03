package com.example.ivalid_compose.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class UserProfile(
    val name: String = "Usuário Ivalid",
    val email: String = "email@nao-logado.com",
    val phone: String? = null,
    val isVerified: Boolean = false,
    val uid: String = ""
)

data class AddressData(
    val cep: String = "",
    val neighborhood: String = "",
    val city: String = "",
    val street: String = "",
    val number: String = "",
    val complement: String = "",
    val state: String = ""
)

data class ProfileUiState(
    val userProfile: UserProfile = UserProfile(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAddressDialogVisible: Boolean = false,
    val addressData: AddressData = AddressData(),
    val isAddressLoading: Boolean = false
)

class ProfileViewModel : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        val user = FirebaseAuth.getInstance().currentUser ?: run {
            uiState = uiState.copy(userProfile = UserProfile(email = "Nenhum usuário logado."), isLoading = false)
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            runCatching {
                val doc = FirebaseFirestore.getInstance()
                    .collection("users").document(user.uid).get().await()

                UserProfile(
                    name = doc.getString("fullName") ?: user.displayName ?: "Cliente Ivalid",
                    email = user.email ?: "Email indisponível",
                    phone = user.phoneNumber,
                    isVerified = user.isEmailVerified,
                    uid = user.uid
                )
            }.onSuccess { profile ->
                uiState = uiState.copy(userProfile = profile, isLoading = false)
            }.onFailure { e ->
                uiState = uiState.copy(error = "Erro ao carregar perfil: ${e.localizedMessage}", isLoading = false)
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        // signOut() é síncrono — não precisa de coroutine
        FirebaseAuth.getInstance().signOut()
        onSuccess()
    }

    fun setAddressDialogVisible(visible: Boolean) {
        uiState = uiState.copy(isAddressDialogVisible = visible)
    }

    fun updateAddressField(field: AddressField, value: String) {
        val addr = uiState.addressData
        uiState = uiState.copy(
            addressData = when (field) {
                AddressField.CEP          -> addr.copy(cep = value)
                AddressField.NEIGHBORHOOD -> addr.copy(neighborhood = value)
                AddressField.CITY         -> addr.copy(city = value)
                AddressField.STREET       -> addr.copy(street = value)
                AddressField.NUMBER       -> addr.copy(number = value)
                AddressField.COMPLEMENT   -> addr.copy(complement = value)
            }
        )
        if (field == AddressField.CEP && value.length == 8) fetchAddressByCep(value)
    }

    private fun fetchAddressByCep(cep: String) {
        uiState = uiState.copy(isAddressLoading = true, error = null)
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    val conn = URL("https://viacep.com.br/ws/$cep/json/").openConnection() as HttpURLConnection
                    conn.connectTimeout = 10_000
                    conn.readTimeout = 10_000
                    val body = conn.inputStream.bufferedReader().use { it.readText() }
                    JSONObject(body)
                }
            }.onSuccess { json ->
                if (json.has("erro")) {
                    uiState = uiState.copy(isAddressLoading = false, error = "CEP não encontrado")
                } else {
                    uiState = uiState.copy(
                        isAddressLoading = false,
                        addressData = uiState.addressData.copy(
                            street = json.optString("logradouro"),
                            neighborhood = json.optString("bairro"),
                            city = json.optString("localidade"),
                            state = json.optString("uf")
                        )
                    )
                }
            }.onFailure { e ->
                uiState = uiState.copy(isAddressLoading = false, error = "Erro na busca do CEP: ${e.localizedMessage}")
            }
        }
    }
}

/** Enum type-safe para campos de endereço — elimina strings mágicas */
enum class AddressField {
    CEP, NEIGHBORHOOD, CITY, STREET, NUMBER, COMPLEMENT
}