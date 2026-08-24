package com.example.myapplication.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// ── Request / Response data classes ───────────────────────────────

data class ChatRequest(
    val message: String,
    val mode: String = "chat",           // "chat" | "emergency" | "pdf"
    val history: List<Map<String, String>> = emptyList(),
    val pdfPath: String = ""
)

data class ChatResponse(
    val response: String,
    val mode: String,
    val success: Boolean,
    val error: String = ""
)

data class HealthResponse(
    val status: String,
    val model: String
)

// ── Endpoints ──────────────────────────────────────────────────────
interface ApiService {

    /** Check if the Python backend is running before trying to chat. */
    @GET("health")
    suspend fun checkHealth(): HealthResponse

    /** Send a chat message and receive the AI response. */
    @POST("chat")
    suspend fun chat(@Body request: ChatRequest): ChatResponse
}
