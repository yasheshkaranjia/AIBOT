package com.example.myapplication.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.ChatMessage

class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.textMessage)
        val bubbleLayout: LinearLayout = view.findViewById(R.id.chatBubbleLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.messageText.text = message.text

        val layoutParams = holder.messageText.layoutParams as LinearLayout.LayoutParams
        if (message.isFromUser) {
            holder.bubbleLayout.gravity = Gravity.END
            holder.messageText.setBackgroundResource(R.drawable.bubble_user_bg)
            layoutParams.gravity = Gravity.END
        } else {
            holder.bubbleLayout.gravity = Gravity.START
            holder.messageText.setBackgroundResource(R.drawable.bubble_ai_bg)
            layoutParams.gravity = Gravity.START
        }
        holder.messageText.layoutParams = layoutParams
    }

    override fun getItemCount() = messages.size
}
