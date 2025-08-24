package com.example.kzneuro_wearos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {
    private lateinit var authenticator: FirebaseAuthenticator
    private lateinit var signInButton: Button
    private lateinit var loadingSpinner: ProgressBar
    private val watchViewModel: WatchViewModel by viewModels()
    private val dataClient by lazy { Wearable.getDataClient(this) }
    private val messageClient by lazy { Wearable.getMessageClient(this) }

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        lifecycleScope.launch {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.result // Throws exception on failure
                val googleIdToken = account.idToken

                if (googleIdToken != null) {
                    // Send the correct token to the watch
                    sendAuthSuccessToWatch(googleIdToken)
                    // Sign in on the phone
                    signInToFirebaseOnPhone(googleIdToken)
                    // Navigate on the phone
                    navigateToHome()
                } else {
                    sendAuthFailureToWatch()
                    showSignInButton()
                }
            } catch (e: Exception) {
                // User cancelled or there was a network error
                sendAuthFailureToWatch()
                showSignInButton()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authenticator = FirebaseAuthenticator(this)
        signInButton = findViewById(R.id.Gsignin_btn) // Use your button ID
        loadingSpinner = findViewById(R.id.loading_spinner)

        signInButton.setOnClickListener {
            showLoading()
            val signInIntent = authenticator.getSignInIntent()
            signInLauncher.launch(signInIntent)
        }
    }

    private suspend fun signInToFirebaseOnPhone(idToken: String) {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            Firebase.auth.signInWithCredential(credential).await()
        } catch (e: Exception) {
            // Log error if phone sign-in fails
        }
    }

    // ... (Your other functions: showLoading, showSignInButton, etc.)

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        watchViewModel.navigateToHomeOnWatch() // Assuming this sends a message to the watch
        startActivity(intent)
        finish()
    }

    private fun sendAuthSuccessToWatch(idToken: String) {
        lifecycleScope.launch {
            try {
                val request = PutDataMapRequest.create("/sign-in-success").apply {
                    dataMap.putString("id_token", idToken)
                }
                dataClient.putDataItem(request.asPutDataRequest().setUrgent()).await()
            } catch (e: Exception) { /* Handle error */ }
        }
    }

    private suspend fun sendAuthFailureToWatch() {
        try {
            val nodes = Wearable.getCapabilityClient(this).getCapability("wearos", 0).await().nodes // Use your actual watch capability name
            nodes.firstOrNull()?.let { node ->
                messageClient.sendMessage(node.id, "/sign-in-failure", null).await()
            }
        } catch(e: Exception) { /* Handle error */ }
    }
    override fun onStart() {
        super.onStart()
        // If user is already signed in, go directly to HomeActivity
        if (Firebase.auth.currentUser != null) {
            navigateToHome()
        }
    }

    private fun showLoading() {
        signInButton.visibility = View.GONE
        loadingSpinner.visibility = View.VISIBLE
    }

    private fun showSignInButton() {
        loadingSpinner.visibility = View.GONE
        signInButton.visibility = View.VISIBLE
    }
}
