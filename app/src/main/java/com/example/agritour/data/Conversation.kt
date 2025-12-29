package com.example.agritour.data

data class Conversation(
    val peerId: String = "",      // The ID of the person you are talking to
    val peerName: String = "",    // Their name (we'll fetch this from Firestore)
    val lastMessage: String = "", // The last message sent in the room
    val roomId: String = "",      // The unique ID (user1_user2)
    val timestamp: Long = 0
)