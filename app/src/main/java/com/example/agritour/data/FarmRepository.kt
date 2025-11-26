package com.example.agritour.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.tasks.await

class FarmRepository {
    private val db = FirebaseFirestore.getInstance()
    private val farmsCollection = db.collection("farms")

    // 1. Fetch all farms
    suspend fun getFarms(): List<Farm> {
        return try {
            val snapshot = farmsCollection.get().await()
            snapshot.toObjects<Farm>()
        } catch (e: Exception) {
            Log.e("FarmRepository", "Error fetching farms", e)
            emptyList()
        }
    }

    // 2. A Helper to seed data (Run this once!)
    fun addDummyData() {
        val dummyFarms = listOf(
            Farm(
                name = "Green Valley Gardens",
                location = "Nairobi, Kenya",
                imageUrl = "https://images.unsplash.com/photo-1500937386664-56d1dfef3854", // Real Image URL
                price = 500,
                rating = 4.8,
                type = "Vegetables"
            ),
            Farm(
                name = "Highlands Coffee Estate",
                location = "Limuru, Kenya",
                imageUrl = "https://images.unsplash.com/photo-1524678606372-8dd0e722650d",
                price = 1200,
                rating = 4.5,
                type = "Coffee"
            ),
            Farm(
                name = "Serenity Herb Farm",
                location = "Naivasha, Kenya",
                imageUrl = "https://images.unsplash.com/photo-1466692476868-aef1dfb1e735",
                price = 850,
                rating = 4.9,
                type = "Herbs"
            )
        )

        dummyFarms.forEach { farm ->
            farmsCollection.add(farm)
                .addOnSuccessListener { Log.d("FarmRepo", "Saved ${farm.name}") }
                .addOnFailureListener { Log.e("FarmRepo", "Failed to save ${farm.name}") }
        }
    }

    private val bookingsCollection = db.collection("bookings")

    suspend fun saveBooking(booking: Booking): Boolean {
        return try {
            bookingsCollection.add(booking).await()
            true // Success
        } catch (e: Exception) {
            Log.e("FarmRepo", "Error saving booking", e)
            false // Failure
        }
    }
}