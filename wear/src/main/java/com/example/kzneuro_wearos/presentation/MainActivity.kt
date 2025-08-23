/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.kzneuro_wearos.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.kzneuro_wearos.R
import com.example.kzneuro_wearos.presentation.theme.KzNeuroWearOsTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    KzNeuroWearOsTheme {
        // ScalingLazyColumn is the primary layout for scrollable content on Wear OS.
        // It optimizes for round displays by scaling items at the top and bottom.
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF111a22)),
            horizontalAlignment = Alignment.CenterHorizontally,
            // Add vertical arrangement to space out the elements appropriately.
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {

            // Item 1: Header Image (Logo)
            item {
                // Making the image circular is a common and aesthetically pleasing
                // pattern on round Wear OS devices.
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "KzNeuro Logo",
                    modifier = Modifier
                        .size(80.dp) // A sensible size for a logo on a watch face
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            // Item 2: Title Text
            item {
                Text(
                    text = "Welcome to KzNeuro",
                    color = Color.White,
                    fontSize = 18.sp, // Text is scaled down for readability on a small screen
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Item 3: Subtitle Text
            item {
                Text(
                    text = "Your personal cognitive health companion.",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // Item 4: "Get Started" Button
            item {
                // The entire element is one Button
                Button(
                    onClick = { /* TODO: Handle Sign-In Click */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp), // A standard, accessible height
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF49A4AF) // android:backgroundTint
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center // Center content within the button
                    ) {
                        // The Google Icon
                        Image(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = "Google Icon",
                            modifier = Modifier.size(24.dp) // A more standard icon size for buttons
                        )

                        // Spacer for visual separation
                        Spacer(modifier = Modifier.width(12.dp))

                        // The text
                        Text(
                            text = "Get Started with Google",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}