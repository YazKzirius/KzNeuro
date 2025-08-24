/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.kzneuro_wearos.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavHostController
import androidx.wear.compose.material.*
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.kzneuro_wearos.SignInViewModel
import com.example.kzneuro_wearos.PhoneAppState
import com.example.kzneuro_wearos.R // Import your R file
import com.example.kzneuro_wearos.presentation.theme.KzNeuroWearOsTheme // Import your theme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)
        setContent {
            // WearApp is the root of your UI
            WearApp()
        }
    }
}

/**
 * The main composable that sets up the app's theme and navigation.
 */
@Composable
fun WearApp() {
    KzNeuroWearOsTheme {
        val navController = rememberSwipeDismissableNavController()

        // This composable will listen for the broadcast from our service
        ListenForAuthResult(navController = navController)

        // Robust Startup Check: Determine the starting screen based on login status.
        val startDestination = if (Firebase.auth.currentUser != null) {
            "home" // If user is already logged in, go straight to home
        } else {
            "welcome" // Otherwise, show the welcome/login screen
        }

        SwipeDismissableNavHost(
            navController = navController,
            startDestination = startDestination // Use the dynamic start destination
        ) {
            composable("welcome") {
                WelcomeScreen(navController = navController)
            }
            composable("home") {
                HomeScreen(navController = navController)
            }
        }
    }
}
/**
 * A composable that registers a broadcast receiver to listen for the
 * authentication result from our DataLayerListenerService.
 */
@Composable
fun ListenForAuthResult(navController: NavHostController) {
    val context = LocalContext.current
    val TAG = "AuthResultListener"

    // This effect runs once and sets up a listener that is cleaned up automatically.
    DisposableEffect(Unit) {
        val authReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val success = intent?.getBooleanExtra("AUTH_SUCCESS", false) ?: false
                Log.d(TAG, "Received broadcast! Success: $success")
                if (success) {
                    // Navigate to home and clear the welcome screen from history
                    navController.navigate("home") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            }
        }

        LocalBroadcastManager.getInstance(context).registerReceiver(
            authReceiver,
            IntentFilter("com.example.kzneuro_wearos.AUTH_RESULT")
        )

        // This block runs when the composable is removed from the screen
        onDispose {
            Log.d(TAG, "Disposing and unregistering broadcast receiver.")
            LocalBroadcastManager.getInstance(context).unregisterReceiver(authReceiver)
        }
    }
}

/**
 * The UI for the initial Welcome Screen.
 * It observes the ViewModel and navigates when the phone is found.
 */
@Composable
fun WelcomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val signInViewModel: SignInViewModel = viewModel()
    val phoneState by signInViewModel.phoneAppState.collectAsState()
    // The UI layout for the welcome screen
    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111a22)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        item {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "KzNeuro Logo",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        item {
            Text(
                text = "Welcome to KzNeuro",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        item {
            Text(
                text = "Your personal cognitive health companion.",
                color = Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
        item {
            val buttonText = when (phoneState) {
                is PhoneAppState.Requesting -> "Finding Phone..."
                is PhoneAppState.PhoneFound -> "Continuing on Phone"
                is PhoneAppState.PhoneNotFound -> "Phone Not Found"
                else -> "Sign in on Phone"
            }

            Button(
                onClick = { signInViewModel.startSignInOnPhone()
                    Toast.makeText(context, buttonText, Toast.LENGTH_SHORT).show()
                          },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF49A4AF)),
                enabled = phoneState !is PhoneAppState.Requesting
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = buttonText,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

/**
 * The UI for the Home Screen, shown after the welcome flow.
 */
@Composable
fun HomeScreen(navController: NavHostController) {
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Text(text = "Home Screen")
        }
    }
}

/**
 * The preview for Android Studio's design canvas.
 */
@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}