package com.example.gameslibraryapp.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

data class UserProfile(
    val username: String,
    val profileImageUrl: String
)

class UserRepository {

    private val auth: FirebaseAuth = Firebase.auth
    private val database: FirebaseDatabase = Firebase.database

    suspend fun getCurrentUserProfile(): UserProfile? {
        val currentUser = auth.currentUser ?: return null
        val userEmail = currentUser.email ?: return null

        return try {
            val usernameSnapshot = database.reference.child("username_to_email")
                .orderByValue()
                .equalTo(userEmail)
                .get()
                .await()

            if (usernameSnapshot.exists()) {
                val username = usernameSnapshot.children.first().key ?: return null
                val imageUrl = "https://api.dicebear.com/8.x/pixel-art/png?seed=$username"
                UserProfile(username, imageUrl)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}