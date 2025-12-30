package com.decentra.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.decentra.app.R
import com.decentra.app.models.Message
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for displaying chat messages in a RecyclerView
 */
class MessageAdapter : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    
    private val messages = mutableListOf<Message>()
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    
    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.messageUsername)
        val messageText: TextView = itemView.findViewById(R.id.messageText)
        val timestamp: TextView = itemView.findViewById(R.id.messageTime)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.username.text = message.username
        holder.messageText.text = message.message
        holder.timestamp.text = dateFormat.format(Date(message.timestamp))
    }
    
    override fun getItemCount(): Int = messages.size
    
    /**
     * Add a new message to the list
     */
    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
    
    /**
     * Add multiple messages to the list
     */
    fun addMessages(newMessages: List<Message>) {
        val startPosition = messages.size
        messages.addAll(newMessages)
        notifyItemRangeInserted(startPosition, newMessages.size)
    }
    
    /**
     * Clear all messages
     */
    fun clearMessages() {
        messages.clear()
        notifyDataSetChanged()
    }
}
