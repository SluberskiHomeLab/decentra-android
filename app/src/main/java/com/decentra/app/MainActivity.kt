package com.decentra.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.decentra.app.databinding.ActivityMainBinding
import com.decentra.app.network.DecentraWebSocketClient
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

/**
 * Main activity for server connection and authentication
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private var webSocketClient: DecentraWebSocketClient? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
    }
    
    private fun setupUI() {
        binding.loginButton.setOnClickListener {
            handleAuth(isSignup = false)
        }
        
        binding.signupButton.setOnClickListener {
            handleAuth(isSignup = true)
        }
        
        updateStatus("Disconnected")
    }
    
    private fun handleAuth(isSignup: Boolean) {
        val serverUrl = binding.serverUrlInput.text.toString().trim()
        val username = binding.usernameInput.text.toString().trim()
        val password = binding.passwordInput.text.toString().trim()
        val inviteCode = binding.inviteCodeInput.text.toString().trim()
        
        // Validate inputs
        if (serverUrl.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show()
            return
        }
        
        // Validate URL format
        if (!serverUrl.startsWith("ws://") && !serverUrl.startsWith("wss://")) {
            Toast.makeText(this, R.string.error_invalid_url, Toast.LENGTH_SHORT).show()
            return
        }
        
        // Disable buttons during connection
        setButtonsEnabled(false)
        updateStatus("Connecting...")
        
        // Create WebSocket client
        webSocketClient = DecentraWebSocketClient(serverUrl, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                runOnUiThread {
                    updateStatus("Connected - Authenticating...")
                }
                
                // Send authentication
                webSocketClient?.authenticate(username, password, inviteCode.ifEmpty { null }, isSignup)
            }
            
            override fun onMessage(webSocket: WebSocket, text: String) {
                runOnUiThread {
                    handleServerMessage(text, serverUrl, username)
                }
            }
            
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                runOnUiThread {
                    updateStatus("Connection failed: ${t.message}")
                    setButtonsEnabled(true)
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.error_connection, t.message),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                runOnUiThread {
                    updateStatus("Disconnected")
                    setButtonsEnabled(true)
                }
            }
        })
        
        webSocketClient?.connect()
    }
    
    private fun handleServerMessage(message: String, serverUrl: String, username: String) {
        try {
            val json = JsonParser.parseString(message).asJsonObject
            
            when {
                json.has("type") -> {
                    val type = json.get("type").asString
                    when (type) {
                        "auth_success", "signup_success" -> {
                            updateStatus("Authenticated!")
                            // Navigate to chat activity
                            val intent = Intent(this, ChatActivity::class.java).apply {
                                putExtra("serverUrl", serverUrl)
                                putExtra("username", username)
                            }
                            
                            // Pass the WebSocket client to chat activity
                            ChatActivity.webSocketClient = webSocketClient
                            
                            startActivity(intent)
                            finish()
                        }
                        "error" -> {
                            val errorMsg = json.get("message")?.asString ?: "Authentication failed"
                            updateStatus("Error: $errorMsg")
                            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
                            setButtonsEnabled(true)
                            webSocketClient?.disconnect()
                        }
                        else -> {
                            updateStatus("Received: $type")
                        }
                    }
                }
                json.has("error") -> {
                    val errorMsg = json.get("error").asString
                    updateStatus("Error: $errorMsg")
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
                    setButtonsEnabled(true)
                    webSocketClient?.disconnect()
                }
                else -> {
                    updateStatus("Received message: $message")
                }
            }
        } catch (e: Exception) {
            updateStatus("Parse error: ${e.message}")
            // Treat plain text as potential success message
            if (message.contains("success", ignoreCase = true) || 
                message.contains("authenticated", ignoreCase = true)) {
                val intent = Intent(this, ChatActivity::class.java).apply {
                    putExtra("serverUrl", serverUrl)
                    putExtra("username", username)
                }
                ChatActivity.webSocketClient = webSocketClient
                startActivity(intent)
                finish()
            }
        }
    }
    
    private fun updateStatus(status: String) {
        binding.statusText.text = "Status: $status"
    }
    
    private fun setButtonsEnabled(enabled: Boolean) {
        binding.loginButton.isEnabled = enabled
        binding.signupButton.isEnabled = enabled
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (ChatActivity.webSocketClient == null) {
            webSocketClient?.disconnect()
        }
    }
}
