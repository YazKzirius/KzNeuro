package com.example.kzneuro_wearos

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {
    private lateinit var authenticator: FirebaseAuthenticator
    private lateinit var signInButton: Button
    private lateinit var loadingSpinner: ProgressBar
    private val watchViewModel: WatchViewModel by viewModels()

    // Modern way to handle activity results, replacing onActivityResult
    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        lifecycleScope.launch {
            val signInSuccessful = authenticator.handleSignInResult(result.data)
            if (signInSuccessful) {
                Toast.makeText(this@MainActivity, "Sign-in successful!", Toast.LENGTH_SHORT).show()
                navigateToHome()
            } else {
                // Show an error and reset the UI
                Toast.makeText(this@MainActivity, "Sign-in failed. Please try again.", Toast.LENGTH_SHORT).show()
                showSignInButton()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Handling click of Get Started Button
        authenticator = FirebaseAuthenticator(this)

        // Initialize views
        signInButton = findViewById(R.id.Gsignin_btn)
        loadingSpinner = findViewById(R.id.loading_spinner)

        // The edge-to-edge boilerplate from your original code
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Handling click of the sign-in button
        signInButton.setOnClickListener {
            showLoading()
            val signInIntent = authenticator.getSignInIntent()
            signInLauncher.launch(signInIntent)
        }
    }

    override fun onStart() {
        super.onStart()
        // If user is already signed in, go directly to HomeActivity
    }

    private fun showLoading() {
        signInButton.visibility = View.GONE
        loadingSpinner.visibility = View.VISIBLE
    }

    private fun showSignInButton() {
        loadingSpinner.visibility = View.GONE
        signInButton.visibility = View.VISIBLE
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        watchViewModel.navigateToHomeOnWatch()
        startActivity(intent)
        finish() // Call finish to remove MainActivity from the back stack
    }
}