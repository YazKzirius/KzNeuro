package com.example.kzneuro_wearos

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * A global CoroutineScope that lives as long as the application itself.
 * Use this for background work that should not be cancelled if a specific
 * screen or service is destroyed.
 */
object ApplicationScope {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
}