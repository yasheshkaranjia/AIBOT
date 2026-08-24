# AIBOT - Android AI Chat Application

A native Android application that provides a chat interface to interact with a local AI backend powered by Ollama and ChromaDB.

## Features
- **Real-time Chat**: Interactive chat UI with distinct user and AI message bubbles.
- **Local AI Integration**: Connects to a local backend server running on port 8000.
- **Asynchronous Networking**: Built with Retrofit and Kotlin Coroutines for smooth, non-blocking network calls.
- **Modern State Management**: Uses `StateFlow` and `SharedFlow` within a `ViewModel` to drive the UI.
- **Material Design**: Clean and responsive layout using Android Material components.

## Technical Stack
- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Networking**: Retrofit 2 & Gson
- **Async Processing**: Kotlin Coroutines & Lifecycle ViewModel KTX
- **UI Components**: RecyclerView, Custom Drawable Shapes, Activity-KTX

## Project Structure
- `ChatActivity.kt`: The main entry point handling UI interactions and lifecycle.
- `MainViewModel.kt`: Manages chat state, message history, and server communication.
- `ChatAdapter.kt`: Handles the rendering of message items in the list.
- `ApiService.kt`: Defines the network endpoints (`/chat`, `/ask`).
- `RetrofitClient.kt`: Singleton for network configuration and API instantiation.

## Getting Started

### Prerequisites
1.  **Backend Server**: Ensure your local AI backend (FastAPI/Uvicorn) is running.
    ```bash
    uvicorn main:app --host 0.0.0.0 --port 8000
    ```
2.  **IP Configuration**: Update the `BASE_URL` in `RetrofitClient.kt` to match your computer's local IP address (currently configured for `192.168.1.177`).

### Running the App
1.  Clone the repository.
2.  Open in Android Studio.
3.  Ensure your Android device/emulator is on the same network as the server.
4.  Build and Run.

## Credits
Developed as part of an AI-driven chatbot project.

---
Created by [yasheshkaranjia](https://github.com/yasheshkaranjia)
