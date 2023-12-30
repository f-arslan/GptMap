package com.espressodev.gptmap.core.screen_capture

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

/**
 * Utility object for managing notifications related to the ScreenCaptureService.
 */
object NotificationUtils {
    // Unique ID for the notification.
    private const val NOTIFICATION_ID = 1337

    // ID for the notification channel.
    private const val NOTIFICATION_CHANNEL_ID = "screen_capture_service_channel"

    // Human-readable name for the channel.
    private const val NOTIFICATION_CHANNEL_NAME = "Screen Capture Service"

    /**
     * Creates a notification for the ScreenCaptureService and returns a Pair of the notification ID and the notification itself.
     * @param context The context used to create the notification.
     * @return A Pair containing the notification ID and the notification.
     */
    fun getNotification(context: Context): Pair<Int, Notification> {
        createNotificationChannel(context)
        val notification = createNotification(context)
        // Notify the system to display the notification.
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.notify(
            NOTIFICATION_ID, notification
        )
        return Pair(NOTIFICATION_ID, notification)
    }

    /**
     * Creates a notification channel if it does not already exist.
     * @param context The context used to create the notification channel.
     */
    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    /**
     * Creates a notification for the ScreenCaptureService.
     * @param context The context used to create the notification.
     * @return The created notification.
     */
    private fun createNotification(context: Context): Notification {
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID).apply {
            setSmallIcon(android.R.drawable.ic_menu_camera) // Use a system icon as an example.
            setContentTitle("Screen Capture Service") // Set the title of the notification.
            setContentText("Tap to return to the app.") // Set the text of the notification.
            setOngoing(true) // Make the notification ongoing.
            setCategory(Notification.CATEGORY_SERVICE) // Categorize as a service.
            setShowWhen(true) // Show the timestamp.
        }.build()
    }
}