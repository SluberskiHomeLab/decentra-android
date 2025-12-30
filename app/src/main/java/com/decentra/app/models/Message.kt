package com.decentra.app.models

import com.google.gson.annotations.SerializedName

/**
 * Represents a chat message
 */
data class Message(
    @SerializedName("username")
    val username: String,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis()
)
