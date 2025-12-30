package com.decentra.app.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.*
import java.util.concurrent.TimeUnit

/**
 * WebSocket client for Decentra server communication
 */
class DecentraWebSocketClient(
    private val serverUrl: String,
    private val listener: WebSocketListener
) {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()
    private val gson = Gson()
    
    // Callback for handling incoming messages in the chat activity
    var onMessageReceived: ((String) -> Unit)? = null
    
    companion object {
        private const val TAG = "DecentraWebSocket"
    }
    
    /**
     * Connect to the Decentra server
     */
    fun connect() {
        val request = Request.Builder()
            .url(serverUrl)
            .build()
        
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket connected")
                listener.onOpen(webSocket, response)
            }
            
            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "Message received: $text")
                listener.onMessage(webSocket, text)
                // Also notify the chat activity if callback is set
                onMessageReceived?.invoke(text)
            }
            
            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closing: $code / $reason")
                listener.onClosing(webSocket, code, reason)
            }
            
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closed: $code / $reason")
                listener.onClosed(webSocket, code, reason)
            }
            
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket error", t)
                listener.onFailure(webSocket, t, response)
            }
        })
    }
    
    /**
     * Authenticate with username and password
     */
    fun authenticate(username: String, password: String, inviteCode: String?, isSignup: Boolean) {
        val authData = JsonObject().apply {
            addProperty("action", if (isSignup) "signup" else "login")
            addProperty("username", username)
            addProperty("password", password)
            if (isSignup && !inviteCode.isNullOrEmpty()) {
                addProperty("invite_code", inviteCode)
            }
        }
        
        sendMessage(gson.toJson(authData))
    }
    
    /**
     * Send a chat message
     */
    fun sendChatMessage(message: String) {
        val messageData = JsonObject().apply {
            addProperty("action", "message")
            addProperty("message", message)
        }
        
        sendMessage(gson.toJson(messageData))
    }
    
    /**
     * Send raw message to WebSocket
     */
    private fun sendMessage(message: String) {
        webSocket?.send(message)
            ?: Log.e(TAG, "WebSocket not connected, cannot send message")
    }
    
    /**
     * Disconnect from server
     */
    fun disconnect() {
        webSocket?.close(1000, "Client closing")
        webSocket = null
    }
    
    /**
     * Check if connected
     */
    fun isConnected(): Boolean {
        return webSocket != null
    }
}
