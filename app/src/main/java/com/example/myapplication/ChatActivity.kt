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

class ChatActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: ChatAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var editMessage: EditText
    private lateinit var buttonSend: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var statusText: TextView

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

        // Set up RecyclerView
        adapter = ChatAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true   // new messages appear at bottom
        }
        recyclerView.adapter = adapter

        // Send button
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

        // Observe all UI state changes in one place
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->

                // Update message list (DiffUtil handles animations automatically)
                adapter.submitList(state.messages.toList()) {
                    // Scroll to bottom after list updates
                    if (state.messages.isNotEmpty()) {
                        recyclerView.smoothScrollToPosition(state.messages.size - 1)
                    }
                }

                // Loading state
                progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                buttonSend.isEnabled = !state.isLoading

                // Backend status indicator
                statusText.text = when (state.backendOnline) {
                    true  -> "● Online"
                    false -> "● Offline — start backend in Termux"
                    null  -> "● Checking..."
                }
                statusText.setTextColor(
                    getColor(if (state.backendOnline == true) R.color.status_online else R.color.status_offline)
                )

                // Show errors as a non-blocking snackbar
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
