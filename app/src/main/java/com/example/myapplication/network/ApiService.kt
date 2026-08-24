package com.example.myapplication.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// ── Request / Response data classes ───────────────────────────────

/**
 * Data class representing a chat request sent to the backend.
 *
 * @property message The user's query text.
 * @property mode The operation mode ("chat", "emergency", "pdf").
 * @property history Optional conversation history.
 * @property pdfPath Optional path to a PDF file for context.
 */
data class ChatRequest(
    val message: String,
    val mode: String = "chat",           // "chat" | "emergency" | "pdf"
    val history: List<Map<String, String>> = emptyList(),
    val pdfPath: String = ""
)

/**
 * Data class representing the backend's response to a chat request.
 *
 * @property response The AI-generated message.
 * @property mode The mode used for this response.
 * @property success True if the request was processed successfully.
 * @property error Error message if success is false.
 */
data class ChatResponse(
    val response: String,
    val mode: String,
    val success: Boolean,
    val error: String = ""
)

/**
 * Data class representing the health status of the backend.
 *
 * @property status Status string (e.g., "ok").
 * @property model Information about the AI model being used.
 */
data class HealthResponse(
    val status: String,
    val model: String
)

/**
 * Interface defining the API endpoints for communicating with the Python backend.
 */
interface ApiService {

    /**
     * Pings the /health endpoint to verify if the backend is running.
     *
     * @return [HealthResponse] containing backend status and model info.
     */
    @GET("health")
    suspend fun checkHealth(): HealthResponse

    /**
     * Sends a chat request and retrieves a response from the AI.
     *
     * @param request The [ChatRequest] containing user message and mode.
     * @return [ChatResponse] containing the AI's reply.
     */
    @POST("chat")
    suspend fun chat(@Body request: ChatRequest): ChatResponse
}
