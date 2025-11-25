package com.example.agritour.data

import com.google.firebase.firestore.DocumentId

data class Farm(
    @DocumentId val id: String = "", // Firestore will auto-fill this ID
    val name: String = "",
    val location: String = "",
    val imageUrl: String = "", // We will store the URL string here
    val price: Int = 0,
    val rating: Double = 0.0,
    val type: String = "Mixed" // e.g., "Dairy", "Coffee", "Vegetables"
)