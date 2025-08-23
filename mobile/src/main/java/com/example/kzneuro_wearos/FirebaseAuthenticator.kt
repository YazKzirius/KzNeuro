package com.example.kzneuro_wearos


import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class FirebaseAuthenticator(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Creates and returns the Intent that launches the Google Sign-In flow.
     */
    fun getSignInIntent(): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.google_client_id)) // Use your server's client ID
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        return googleSignInClient.signInIntent
    }

    /**
     * Handles the result from the Google Sign-In Intent.
     * If successful, it authenticates with Firebase.
     *
     * @param intent The data Intent received from onActivityResult.
     * @return true if Firebase authentication was successful, false otherwise.
     */
    suspend fun handleSignInResult(intent: Intent?): Boolean {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            val account = task.result // This will throw an exception on failure
            val idToken = account.idToken ?: return false // idToken is crucial for Firebase

            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await()
            true // Sign-in successful
        } catch (e: Exception) {
            // Handle exceptions (e.g., user canceled, network error)
            e.printStackTrace()
            false // Sign-in failed
        }
    }

    /**
     * Checks if a user is currently signed in.
     */
    fun getCurrentUser() = auth.currentUser

    /**
     * Signs out the current user.
     */
    fun signOut() {
        auth.signOut()
    }
}