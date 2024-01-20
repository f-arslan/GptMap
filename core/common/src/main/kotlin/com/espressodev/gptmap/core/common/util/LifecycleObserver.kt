package com.espressodev.gptmap.core.common.util

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun LifecycleObserver(tag: String) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycle = lifecycleOwner.lifecycle

    val currentLifecycle = rememberUpdatedState(newValue = lifecycle.currentState)

    DisposableEffect(currentLifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    Log.d(tag, "ON_CREATE")
                }

                Lifecycle.Event.ON_START -> {
                    Log.d(tag, "ON_START")
                }

                Lifecycle.Event.ON_RESUME -> {
                    Log.d(tag, "ON_RESUME")
                }

                Lifecycle.Event.ON_PAUSE -> Log.d(tag, "ON_PAUSE")
                Lifecycle.Event.ON_STOP -> Log.d(tag, "ON_STOP")
                Lifecycle.Event.ON_DESTROY -> Log.d(tag, "ON_DESTROY")
                Lifecycle.Event.ON_ANY -> Log.d(tag, "ON_ANY")
            }
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}
