package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.ChatAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var editMessage: EditText
    private lateinit var buttonSend: ImageButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recyclerView = findViewById(R.id.recyclerViewChat)
        editMessage = findViewById(R.id.editMessage)
        buttonSend = findViewById(R.id.buttonSend)
        progressBar = findViewById(R.id.progressBar)

        adapter = ChatAdapter(viewModel.chatMessages)
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        recyclerView.adapter = adapter

        buttonSend.setOnClickListener {
            val message = editMessage.text.toString()
            if (message.isNotBlank()) {
                viewModel.sendMessage(message)
                editMessage.text.clear()
            }
        }

        lifecycleScope.launch {
            viewModel.messagesFlow.collectLatest {
                adapter.notifyDataSetChanged()
                if (viewModel.chatMessages.isNotEmpty()) {
                    recyclerView.smoothScrollToPosition(viewModel.chatMessages.size - 1)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { loading ->
                progressBar.visibility = if (loading) View.VISIBLE else View.GONE
                buttonSend.isEnabled = !loading
            }
        }
    }
}
