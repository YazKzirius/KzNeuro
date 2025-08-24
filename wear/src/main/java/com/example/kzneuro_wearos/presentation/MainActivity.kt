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
import com.example.kzneuro_wearos.OpenAppViewModel
import com.example.kzneuro_wearos.PhoneAppState
import com.example.kzneuro_wearos.R // Import your R file
import com.example.kzneuro_wearos.presentation.theme.KzNeuroWearOsTheme // Import your theme

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
        ListenForNavigationCommands(navController = navController)

        // SwipeDismissableNavHost is the navigation container. It decides which
        // screen to show based on the current "route".
        SwipeDismissableNavHost(
            navController = navController,
            startDestination = "welcome" // The app will start at the "welcome" route
        ) {
            // Define the "welcome" screen's content
            composable("welcome") {
                WelcomeScreen(navController = navController)
            }

            // Define the "home" screen's content
            composable("home") {
                HomeScreen(navController = navController)
            }
        }
    }
}
// NEW COMPOSABLE TO HANDLE THE BROADCAST LISTENER
@Composable
fun ListenForNavigationCommands(navController: NavHostController) {
    val context = LocalContext.current

    // DisposableEffect is used to register a listener when the composable
    // enters the screen and unregister it when it leaves, preventing memory leaks.
    DisposableEffect(Unit) {
        // 1. Create the BroadcastReceiver
        val navigationReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                // When the broadcast is received, navigate to the settings screen
                if (intent?.action == "com.example.kzneuro_wearos.NAVIGATE_TO_HOME") {
                    navController.navigate("home")
                }
            }
        }

        // 2. Register the receiver to listen for our specific action
        LocalBroadcastManager.getInstance(context).registerReceiver(
            navigationReceiver,
            IntentFilter("com.example.kzneuro_wearos.NAVIGATE_TO_HOME")
        )

        // 3. The onDispose block is called when the composable leaves the screen
        onDispose {
            // Unregister the receiver to prevent memory leaks
            LocalBroadcastManager.getInstance(context).unregisterReceiver(navigationReceiver)
        }
    }
}

/**
 * The UI for the initial Welcome Screen.
 * It observes the ViewModel and navigates when the phone is found.
 */
@Composable
fun WelcomeScreen(navController: NavHostController) {
    val openAppViewModel: OpenAppViewModel = viewModel()
    val phoneState by openAppViewModel.phoneAppState.collectAsState()
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
                onClick = { openAppViewModel.openAppOnPhone() },
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