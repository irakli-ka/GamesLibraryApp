package com.example.gameslibraryapp.repository

import com.example.gameslibraryapp.model.Game
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

data class UserProfile(
    val username: String,
    val email: String,
    val profileImageUrl: String = "https://api.dicebear.com/8.x/pixel-art/png?seed=$username"
)

class UserRepository {

    private val auth: FirebaseAuth = Firebase.auth
    private val database: FirebaseDatabase = Firebase.database

    suspend fun getCurrentUserProfile(): UserProfile? {
        val currentUser = auth.currentUser ?: return null
        val uid = currentUser.uid

        return try {
            val snapshot = database.reference.child("users").child(uid).get().await()

            if (snapshot.exists()) {
                val username = snapshot.child("username").value as? String ?: ""
                val email = snapshot.child("email").value as? String ?: ""

                UserProfile(username,  email)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    suspend fun addGameToLibrary(game: Game) {
        val currentUser = auth.currentUser ?: return
        val userId = currentUser.uid

        database.reference
            .child("user_library")
            .child(userId)
            .child(game.id.toString())
            .setValue(game)
            .await()
    }

    suspend fun removeGameFromLibrary(gameId: Int) {
        val userId = auth.currentUser?.uid ?: return
        database.reference
            .child("user_library")
            .child(userId)
            .child(gameId.toString())
            .removeValue()
            .await()
    }
}