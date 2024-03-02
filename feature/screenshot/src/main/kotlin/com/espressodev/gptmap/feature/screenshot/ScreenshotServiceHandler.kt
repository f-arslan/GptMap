package com.espressodev.gptmap.feature.screenshot

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.espressodev.gptmap.core.save_screenshot.SaveScreenshotService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class ScreenshotServiceHandler(private val applicationContext: Context) {
    private val screenshotStateChannel = Channel<ScreenshotState>(Channel.CONFLATED)
    val screenshotStateFlow = screenshotStateChannel.receiveAsFlow()

    private val serviceStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                SaveScreenshotService.ACTION_SERVICE_STARTED ->
                    screenshotStateChannel.trySend(ScreenshotState.STARTED)
                SaveScreenshotService.ACTION_SERVICE_STOPPED ->
                    screenshotStateChannel.trySend(ScreenshotState.FINISHED)
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun registerServiceStateReceiver() {
        val filter = IntentFilter().apply {
            addAction(SaveScreenshotService.ACTION_SERVICE_STARTED)
            addAction(SaveScreenshotService.ACTION_SERVICE_STOPPED)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            applicationContext.registerReceiver(serviceStateReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            applicationContext.registerReceiver(serviceStateReceiver, filter)
        }
    }

    fun unregisterServiceStateReceiver() {
        applicationContext.unregisterReceiver(serviceStateReceiver)
    }
}

enum class ScreenshotState {
    IDLE, STARTED, FINISHED
}
