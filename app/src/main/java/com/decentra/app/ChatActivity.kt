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
        if (webSocketClient == null) {
            Toast.makeText(this, "Not connected to server", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        // Set up callback to receive messages from the server
        webSocketClient?.onMessageReceived = { messageText ->
            runOnUiThread {
                handleIncomingMessage(messageText)
            }
        }
    }
    
    private fun handleIncomingMessage(messageText: String) {
        try {
            val json = JsonParser.parseString(messageText).asJsonObject
            
            // Check if this is a chat message
            if (json.has("type") && json.get("type").asString == "message") {
                val username = json.get("username")?.asString ?: "Unknown"
                val message = json.get("message")?.asString ?: ""
                
                val chatMessage = Message(
                    username = username,
                    message = message,
                    timestamp = System.currentTimeMillis()
                )
                
                messageAdapter.addMessage(chatMessage)
                binding.messagesRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
            } else if (json.has("type") && json.get("type").asString == "history") {
                // Handle message history
                val messages = json.getAsJsonArray("messages")
                messages?.forEach { messageElement ->
                    val msgObj = messageElement.asJsonObject
                    val username = msgObj.get("username")?.asString ?: "Unknown"
                    val message = msgObj.get("message")?.asString ?: ""
                    
                    val chatMessage = Message(
                        username = username,
                        message = message,
                        timestamp = System.currentTimeMillis()
                    )
                    messageAdapter.addMessage(chatMessage)
                }
                binding.messagesRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
            }
        } catch (e: Exception) {
            // If parsing fails, it might be a plain text message
            // Just log it for now
            android.util.Log.e("ChatActivity", "Error parsing message: $messageText", e)
        }
    }
    
    private fun sendMessage() {
        val messageText = binding.messageInput.text.toString().trim()
        
        if (messageText.isEmpty()) {
            return
        }
        
        webSocketClient?.sendChatMessage(messageText)
        
        // Clear input immediately
        binding.messageInput.text?.clear()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Clear the callback
        webSocketClient?.onMessageReceived = null
        webSocketClient?.disconnect()
        webSocketClient = null
    }
}
