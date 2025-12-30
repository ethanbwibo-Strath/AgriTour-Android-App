package com.example.agritour.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

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

    suspend fun updateProfile(newName: String, newEmail: String): String? {
        return try {
            val user = auth.currentUser ?: return "Not logged in"

            // Update Firestore first (less sensitive)
            val updates = mapOf(
                "name" to newName,
                "email" to newEmail
            )
            db.collection("users").document(user.uid).update(updates).await()

            // Sensitive: Only update email if it actually changed
            if (newEmail != user.email) {
                user.updateEmail(newEmail).await()
            }

            null
        } catch (e: Exception) {
            // This will catch the "Requires Recent Login" error
            e.localizedMessage ?: "An error occurred"
        }
    }

    suspend fun uploadProfileImage(imageUri: android.net.Uri): String? {
        return try {
            val userId = auth.currentUser?.uid ?: return null
            // Create a reference to "profile_images/USER_ID.jpg"
            val fileRef = storage.reference.child("profile_images/$userId.jpg")

            // Upload the file
            fileRef.putFile(imageUri).await()

            // Get the downloadable URL
            val downloadUrl = fileRef.downloadUrl.await().toString()

            // Update Firestore with the new image URL
            db.collection("users").document(userId)
                .update("profileImageUrl", downloadUrl).await()

            null // Success
        } catch (e: Exception) {
            e.localizedMessage
        }
    }
}