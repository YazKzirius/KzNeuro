package com.example.kzneuro_wearos


import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

class FirebaseAuthenticator {
    private val auth = Firebase.auth
    private val TAG = "WatchAuthenticator"

    suspend fun signInWithIdToken(idToken: String): Boolean {
        return try {
            Log.d(TAG, "Attempting to sign in on watch with received token...")
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await()
            Log.d(TAG, "Sign-in on watch SUCCESSFUL. User: ${auth.currentUser?.uid}")
            true // Success
        } catch (e: Exception) {
            Log.e(TAG, "Sign-in on watch FAILED.", e)
            false // Failure
        }
    }

    fun getCurrentUser() = auth.currentUser
}