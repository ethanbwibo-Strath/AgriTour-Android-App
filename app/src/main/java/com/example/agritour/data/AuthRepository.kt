package com.example.agritour.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Returns NULL if success, or an ERROR MESSAGE if failed
    suspend fun signUp(name: String, email: String, pass: String, isFarmer: Boolean): String? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val userId = result.user?.uid ?: return "User creation failed"

            val user = hashMapOf(
                "uid" to userId,
                "name" to name,
                "email" to email,
                "role" to if (isFarmer) "farmer" else "visitor",
                "createdAt" to com.google.firebase.Timestamp.now()
            )

            db.collection("users").document(userId).set(user).await()
            null // Success!
        } catch (e: Exception) {
            e.localizedMessage // Return the actual error from Firebase
        }
    }

    // Returns NULL if success, or an ERROR MESSAGE if failed
    suspend fun login(email: String, pass: String): String? {
        return try {
            auth.signInWithEmailAndPassword(email, pass).await()
            null // Success!
        } catch (e: Exception) {
            e.localizedMessage
        }
    }

    suspend fun sendPasswordReset(email: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun signOut() {
        auth.signOut()
    }
}