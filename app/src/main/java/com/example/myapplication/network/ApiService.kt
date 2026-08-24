package com.example.myapplication.network

import retrofit2.http.Body
import retrofit2.http.POST

data class AskRequest(val prompt: String)
data class AskResponse(val response: String)

data class ChatRequest(val message: String, val mode: String = "chat")
data class ChatResponse(val response: String)

interface ApiService {
    @POST("/ask")
    suspend fun ask(@Body request: AskRequest): AskResponse

    @POST("/chat")
    suspend fun chat(@Body request: ChatRequest): ChatResponse
}
