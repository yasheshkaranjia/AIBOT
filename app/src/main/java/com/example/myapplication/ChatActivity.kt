package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.ChatAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Activity responsible for the chat interface.
 * Handles user interactions, displays messages, and communicates with the [MainViewModel].
 */
class ChatActivity : AppCompatActivity() {

    /** ViewModel to manage the UI state and business logic. */
    private val viewModel: MainViewModel by viewModels()
    /** Adapter to handle the display of chat messages in the RecyclerView. */
    private lateinit var adapter: ChatAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var editMessage: EditText
    private lateinit var buttonSend: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var statusText: TextView

    /**
     * Initializes the activity, sets up the RecyclerView, and starts observing the UI state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Receive the mode chosen on the main menu
        val mode = intent.getStringExtra("mode") ?: "chat"
        viewModel.currentMode = mode

        recyclerView = findViewById(R.id.recyclerViewChat)
        editMessage = findViewById(R.id.editMessage)
        buttonSend = findViewById(R.id.buttonSend)
        progressBar = findViewById(R.id.progressBar)
        statusText = findViewById(R.id.statusText)

        // Set title based on mode
        title = when (mode) {
            "emergency" -> "🆘 Emergency Knowledge"
            "pdf"       -> "📚 PDF Reader"
            else        -> "💬 General Chat"
        }

        // Set up RecyclerView with a LinearLayoutManager that stacks items from the end
        adapter = ChatAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true   // new messages appear at bottom
        }
        recyclerView.adapter = adapter

        // Handle send button click to submit user message
        buttonSend.setOnClickListener {
            val text = editMessage.text.toString().trim()
            if (text.isNotBlank()) {
                viewModel.sendMessage(text)
                editMessage.text.clear()
            }
        }

        // Also send on keyboard "Send" action
        editMessage.setOnEditorActionListener { _, _, _ ->
            buttonSend.performClick()
            true
        }

        // Observe all UI state changes from the ViewModel and update the UI accordingly
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->

                // Update message list (DiffUtil handles animations automatically)
                adapter.submitList(state.messages.toList()) {
                    // Scroll to bottom after list updates to keep the latest message visible
                    if (state.messages.isNotEmpty()) {
                        recyclerView.smoothScrollToPosition(state.messages.size - 1)
                    }
                }

                // Update loading state: show progress bar and disable send button while loading
                progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                buttonSend.isEnabled = !state.isLoading

                // Update backend status indicator (Online/Offline/Checking)
                statusText.text = when (state.backendOnline) {
                    true  -> "● Online"
                    false -> "● Offline — start backend in Termux"
                    null  -> "● Checking..."
                }
                statusText.setTextColor(
                    getColor(if (state.backendOnline == true) R.color.status_online else R.color.status_offline)
                )

                // Show errors as a non-blocking snackbar if an error message exists
                state.errorMessage?.let { error ->
                    Snackbar.make(recyclerView, error, Snackbar.LENGTH_LONG)
                        .setAction("OK") { viewModel.errorShown() }
                        .show()
                    viewModel.errorShown()
                }
            }
        }
    }
}
