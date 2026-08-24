package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.ChatMessage
import com.example.myapplication.network.ChatRequest
import com.example.myapplication.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Everything the UI needs, collected in one place. */
data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,   // null = no error to show
    val backendOnline: Boolean? = null  // null = not yet checked
)

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // The mode is set once when the activity opens (chat / emergency / pdf).
    var currentMode: String = "chat"

    init {
        checkBackendHealth()
    }

    /** Ping /health so the UI can show "backend offline" early. */
    private fun checkBackendHealth() {
        viewModelScope.launch {
            try {
                RetrofitClient.api.checkHealth()
                _uiState.update { it.copy(backendOnline = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(backendOnline = false) }
            }
        }
    }

    fun sendMessage(text: String) {
        // Add the user's message to the list immediately
        val userMsg = ChatMessage(text.trim(), isFromUser = true)
        _uiState.update { state ->
            state.copy(
                messages = state.messages + userMsg,
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.chat(
                    ChatRequest(message = text.trim(), mode = currentMode)
                )

                val aiMsg = if (response.success) {
                    ChatMessage(response.response, isFromUser = false)
                } else {
                    ChatMessage("⚠️ Backend error: ${response.error}", isFromUser = false)
                }

                _uiState.update { state ->
                    state.copy(
                        messages = state.messages + aiMsg,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                val hint = if (e.message?.contains("refused") == true)
                    "Make sure the Python backend is running in Termux."
                else e.message ?: "Unknown error"

                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = hint
                    )
                }
            }
        }
    }

    /** Called after the error snackbar has been shown so it doesn't repeat. */
    fun errorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
