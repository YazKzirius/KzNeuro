package com.example.kzneuro_wearos.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip

@Composable
fun SettingsScreen() {
    // ScalingLazyColumn is the standard for scrollable lists on Wear OS
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(text = "App Settings")
        }
    }
}