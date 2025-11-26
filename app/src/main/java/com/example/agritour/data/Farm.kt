package com.example.agritour.data

import com.google.firebase.firestore.DocumentId

data class Farm(
    @DocumentId val id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val location: String = "",
    val imageUrl: String = "",
    val price: Int = 0,
    val rating: Double = 0.0,
    val type: String = "Mixed",
    val description: String = ""
)