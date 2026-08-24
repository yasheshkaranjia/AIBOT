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
/**
 * RecyclerView adapter for displaying chat messages.
 * Inherits from [ListAdapter] to leverage [DiffUtil] for efficient list updates and animations.
 */
class ChatAdapter : ListAdapter<ChatMessage, ChatAdapter.ChatViewHolder>(DiffCallback) {

    /**
     * Callback for calculating the diff between two non-null items in a list.
     * Helps [ListAdapter] determine which items to animate or refresh.
     */
    object DiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        /** Checks if two objects represent the same item (based on timestamp and sender). */
        override fun areItemsTheSame(old: ChatMessage, new: ChatMessage): Boolean =
            old.timestamp == new.timestamp && old.isFromUser == new.isFromUser

        /** Checks if the contents of two items are equal. */
        override fun areContentsTheSame(old: ChatMessage, new: ChatMessage): Boolean =
            old == new
    }

    /**
     * ViewHolder for a single chat message item.
     * Holds references to the bubble layout, text, and timestamp views.
     */
    class ChatViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_chat_message, parent, false)
    ) {
        val bubbleLayout: LinearLayout = itemView.findViewById(R.id.chatBubbleLayout)
        val messageText: TextView = itemView.findViewById(R.id.textMessage)
        val timestampText: TextView = itemView.findViewById(R.id.textTimestamp)
    }

    /** Creates a new [ChatViewHolder] when needed. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ChatViewHolder(parent)

    /** Binds message data to the ViewHolder and handles alignment/styling based on sender. */
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = getItem(position)

        holder.messageText.text = message.text
        holder.timestampText.text = message.formattedTime()

        // Align bubble to the right for user messages, left for AI responses
        if (message.isFromUser) {
            holder.bubbleLayout.gravity = Gravity.END
            holder.messageText.setBackgroundResource(R.drawable.bubble_user_bg)
        } else {
            holder.bubbleLayout.gravity = Gravity.START
            holder.messageText.setBackgroundResource(R.drawable.bubble_ai_bg)
        }
    }
}
