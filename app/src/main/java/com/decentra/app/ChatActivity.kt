package com.decentra.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.decentra.app.adapters.MessageAdapter
import com.decentra.app.databinding.ActivityChatBinding
import com.decentra.app.models.Message
import com.decentra.app.network.DecentraWebSocketClient
import com.google.gson.JsonParser
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

/**
 * Chat activity for sending and receiving messages
 */
class ChatActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityChatBinding
    private lateinit var messageAdapter: MessageAdapter
    private var currentUsername: String = ""
    
    companion object {
        var webSocketClient: DecentraWebSocketClient? = null
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        currentUsername = intent.getStringExtra("username") ?: ""
        val serverUrl = intent.getStringExtra("serverUrl") ?: ""
        
        title = "Decentra - $currentUsername"
        
        setupRecyclerView()
        setupUI()
        setupWebSocketListener()
    }
    
    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter()
        binding.messagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity).apply {
                stackFromEnd = true
            }
            adapter = messageAdapter
        }
    }
    
    private fun setupUI() {
        binding.sendButton.setOnClickListener {
            sendMessage()
        }
        
        binding.messageInput.setOnEditorActionListener { _, _, _ ->
            sendMessage()
            true
        }
    }
    
    private fun setupWebSocketListener() {
        // The WebSocket is already connected from MainActivity
        // We just need to update the listener to handle chat messages
        if (webSocketClient == null) {
            Toast.makeText(this, "Not connected to server", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
    }
    
    private fun sendMessage() {
        val messageText = binding.messageInput.text.toString().trim()
        
        if (messageText.isEmpty()) {
            return
        }
        
        webSocketClient?.sendChatMessage(messageText)
        
        // Add message to UI optimistically
        val message = Message(
            username = currentUsername,
            message = messageText,
            timestamp = System.currentTimeMillis()
        )
        messageAdapter.addMessage(message)
        
        // Clear input and scroll to bottom
        binding.messageInput.text?.clear()
        binding.messagesRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        webSocketClient?.disconnect()
        webSocketClient = null
    }
}
