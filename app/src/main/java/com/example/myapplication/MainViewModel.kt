package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.ChatMessage
import com.example.myapplication.network.AskRequest
import com.example.myapplication.network.ChatRequest
import com.example.myapplication.network.RetrofitClient
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _chatMessages = mutableListOf<ChatMessage>()
    val chatMessages: List<ChatMessage> get() = _chatMessages

    private val _messagesFlow = MutableSharedFlow<Unit>(replay = 1)
    val messagesFlow: SharedFlow<Unit> = _messagesFlow

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _response = MutableStateFlow("")
    val response: StateFlow<String> = _response

    fun sendMessage(text: String) {
        val userMessage = ChatMessage(text, isFromUser = true)
        _chatMessages.add(userMessage)
        viewModelScope.launch {
            _messagesFlow.emit(Unit)
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = RetrofitClient.api.chat(ChatRequest(text))
                val aiMessage = ChatMessage(result.response, isFromUser = false)
                _chatMessages.add(aiMessage)
                _messagesFlow.emit(Unit)
            } catch (e: Exception) {
                val errorMessage = ChatMessage("Error: ${e.message}", isFromUser = false)
                _chatMessages.add(errorMessage)
                _messagesFlow.emit(Unit)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun ask(prompt: String) {
        viewModelScope.launch {
            try {
                val result = RetrofitClient.api.ask(AskRequest(prompt))
                _response.value = result.response
            } catch (e: Exception) {
                _response.value = "Error: ${e.message}"
            }
        }
    }
}
