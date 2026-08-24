package com.example.myapplication.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.ChatMessage

/**
 * ListAdapter automatically calculates the diff between old and new lists
 * and only animates the items that actually changed.
 * This replaces the old notifyDataSetChanged() approach which rebuilt everything.
 */
class ChatAdapter : ListAdapter<ChatMessage, ChatAdapter.ChatViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        // Two messages are the "same item" if they share the same timestamp + sender
        override fun areItemsTheSame(old: ChatMessage, new: ChatMessage): Boolean =
            old.timestamp == new.timestamp && old.isFromUser == new.isFromUser

        // Content equality — Kotlin data class handles this automatically
        override fun areContentsTheSame(old: ChatMessage, new: ChatMessage): Boolean =
            old == new
    }

    class ChatViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_chat_message, parent, false)
    ) {
        val bubbleLayout: LinearLayout = itemView.findViewById(R.id.chatBubbleLayout)
        val messageText: TextView = itemView.findViewById(R.id.textMessage)
        val timestampText: TextView = itemView.findViewById(R.id.textTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ChatViewHolder(parent)

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = getItem(position)

        holder.messageText.text = message.text
        holder.timestampText.text = message.formattedTime()

        if (message.isFromUser) {
            holder.bubbleLayout.gravity = Gravity.END
            holder.messageText.setBackgroundResource(R.drawable.bubble_user_bg)
        } else {
            holder.bubbleLayout.gravity = Gravity.START
            holder.messageText.setBackgroundResource(R.drawable.bubble_ai_bg)
        }
    }
}
