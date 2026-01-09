package com.example.agritour.data

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Booking(
    @DocumentId val id: String = "",
    val farmId: String = "",
    val farmName: String = "",
    val userId: String = "",
    val farmOwnerId: String = "",
    val date: String = "",
    val time: String = "",
    val groupSize: Int = 1,
    val totalPrice: Int = 0,
    val status: String = "Pending",
    val paymentMethod: String = "",
    val timestamp: Long = System.currentTimeMillis()
)