package com.example.agritour.data

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Booking(
    @DocumentId val id: String = "",
    val farmId: String = "",
    val farmName: String = "", // Store name for easier display later
    val userId: String = "test_user_1", // Hardcoded until we add Login
    val date: String = "",
    val time: String = "",
    val groupSize: Int = 1,
    val totalPrice: Int = 0,
    val status: String = "Pending", // Pending, Confirmed, Cancelled
    val paymentMethod: String = "",
    @ServerTimestamp val createdAt: Date? = null
)