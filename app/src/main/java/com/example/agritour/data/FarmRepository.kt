package com.example.agritour.data

import android.util.Log
import com.example.agritour.utils.ImageUtils
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.tasks.await
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FarmRepository {
    private val db = FirebaseFirestore.getInstance()
    private val farmsCollection = db.collection("farms")
    private val storage = com.google.firebase.storage.FirebaseStorage.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

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

    suspend fun createBooking(booking: Booking): Boolean {
        return try {
            val bookingRef = db.collection("bookings").document()
            val finalBooking = booking.copy(id = bookingRef.id)
            bookingRef.set(finalBooking).await()
            true
        } catch (e: Exception) {
            Log.e("FarmRepo", "Booking failed", e)
            false
        }
    }

    suspend fun getBookingsForUser(userId: String): List<Booking> {
        return try {
            val snapshot = bookingsCollection
                .whereEqualTo("userId", userId)
                // .orderBy("createdAt", Query.Direction.DESCENDING) // Requires a Firestore Index (skip for now to avoid crashes)
                .get()
                .await()
            snapshot.toObjects<Booking>()
        } catch (e: Exception) {
            Log.e("FarmRepo", "Error fetching bookings", e)
            emptyList()
        }
    }

    suspend fun cancelBooking(bookingId: String): Boolean {
        return try {
            bookingsCollection.document(bookingId)
                .update("status", "Cancelled")
                .await()
            true
        } catch (e: Exception) {
            Log.e("FarmRepo", "Error cancelling booking", e)
            false
        }
    }

    suspend fun getBooking(bookingId: String): Booking? {
        return try {
            val document = bookingsCollection.document(bookingId).get().await()
            document.toObject<Booking>()
        } catch (e: Exception) {
            Log.e("FarmRepo", "Error getting booking", e)
            null
        }
    }

    private val usersCollection = db.collection("users")

    suspend fun getUserProfile(userId: String): UserProfile? {
        return try {
            val snapshot = usersCollection.document(userId).get().await()
            snapshot.toObject<UserProfile>()
        } catch (e: Exception) {
            Log.e("FarmRepo", "Error fetching user profile", e)
            null
        }
    }

    suspend fun getFarmsByOwner(ownerId: String): List<Farm> {
        return try {
            val snapshot = farmsCollection
                .whereEqualTo("ownerId", ownerId) // We need to make sure we save this field later!
                .get()
                .await()
            snapshot.toObjects<Farm>()
        } catch (e: Exception) {
            Log.e("FarmRepo", "Error fetching my farms", e)
            emptyList()
        }
    }

    suspend fun uploadImage(imageUri: android.net.Uri, context: android.content.Context): String? {
        return try {
            val compressedData = ImageUtils.compressImage(context, imageUri)
                ?: return null

            val filename = "farms/${System.currentTimeMillis()}.jpg"
            val ref = storage.reference.child(filename)

            ref.putBytes(compressedData).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e("FarmRepo", "Image upload failed", e)
            null
        }
    }

    suspend fun addFarm(farm: Farm): Boolean {
        return try {
            farmsCollection.add(farm).await()
            true
        } catch (e: Exception) {
            Log.e("FarmRepo", "Error adding farm", e)
            false
        }
    }

    suspend fun getBookingsForOwner(ownerId: String): List<Booking> {
        return try {
            val snapshot = bookingsCollection
                .whereEqualTo("farmOwnerId", ownerId)
                .get()
                .await()
            snapshot.toObjects<Booking>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun sendMessage(conversationId: String, message: ChatMessage) {
        val messageRef = database.child("chats").child(conversationId).push() // Generates unique ID
        val messageWithId = message.copy(id = messageRef.key ?: "")
        messageRef.setValue(messageWithId).await()
    }

    fun getMessages(conversationId: String): Flow<List<ChatMessage>> = callbackFlow {
        val chatRef = database.child("chats").child(conversationId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { it.getValue(ChatMessage::class.java) }
                trySend(messages) // Emit new list to the UI
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        chatRef.addValueEventListener(listener)
        awaitClose { chatRef.removeEventListener(listener) } // Cleanup when screen closes
    }
}