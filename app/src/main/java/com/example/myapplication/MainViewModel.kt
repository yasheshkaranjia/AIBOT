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
/**
 * Represents the state of the Chat UI.
 *
 * @property messages List of chat messages to display.
 * @property isLoading Indicates if a response is being fetched from the backend.
 * @property errorMessage Error message to display to the user, if any.
 * @property backendOnline Status of the backend connectivity (true=online, false=offline, null=checking).
 */
data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,   // null = no error to show
    val backendOnline: Boolean? = null  // null = not yet checked
)

/**
 * ViewModel for managing chat logic and state.
 * It coordinates interaction between the UI and the networking layer.
 */
class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    /** The observable UI state. */
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    /** The current operational mode of the chat (e.g., "chat", "emergency", "pdf"). */
    var currentMode: String = "chat"

    init {
        // Automatically check backend health on initialization
        checkBackendHealth()
    }

    /**
     * Checks if the backend API is reachable by pinging the /health endpoint.
     * Updates the UI state with the backend status.
     */
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

    /**
     * Sends a message to the backend and updates the UI state with the response.
     *
     * @param text The message text to send.
     */
    fun sendMessage(text: String) {
        // Add the user's message to the list immediately for instant feedback
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
                // Call the API
                val response = RetrofitClient.api.chat(
                    ChatRequest(message = text.trim(), mode = currentMode)
                )

                // Handle the response
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
                // Handle network errors or server unavailability
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

    /**
     * Resets the error message in the UI state.
     * Should be called after the error has been successfully displayed to the user.
     */
    fun errorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
