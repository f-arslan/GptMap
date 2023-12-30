package com.espressodev.gptmap.core.screen_capture

import android.app.Activity
import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


/**
 * Service for capturing the device's screen.
 */
class ScreenCaptureService : Service() {
    private var mMediaProjection: MediaProjection? = null
    private var mStoreDir: String? = null
    private var mImageReader: ImageReader? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    private var mDensity = 0
    private var mWidth = 0
    private var mHeight = 0
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    /**
     * Listener for new images available for processing.
     */
    private inner class ImageAvailableListener : ImageReader.OnImageAvailableListener {
        override fun onImageAvailable(reader: ImageReader) {
            serviceScope.launch {
                var fos: FileOutputStream? = null
                var bitmap: Bitmap? = null
                try {
                    reader.acquireLatestImage()?.use { image ->
                        val planes = image.planes
                        val buffer = planes[0].buffer
                        val pixelStride = planes[0].pixelStride
                        val rowStride = planes[0].rowStride
                        val rowPadding = rowStride - pixelStride * mWidth

                        // Create bitmap
                        bitmap = Bitmap.createBitmap(
                            mWidth + rowPadding / pixelStride,
                            mHeight,
                            Bitmap.Config.ARGB_8888
                        )
                        bitmap?.copyPixelsFromBuffer(buffer)

                        fos = FileOutputStream("$mStoreDir/screenshot.png")
                        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, fos!!)
                        Log.e(TAG, "Screenshot captured")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    fos?.close()
                    bitmap?.recycle()
                    stopSelf() // Stop the service after taking the screenshot
                }
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        // This is a started service, not a bound service, so return null
        return null
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize the store directory for screenshots
        initializeStoreDirectory()
    }

    @Suppress("DEPRECATION")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Start the service in the foreground with a notification
        startForegroundWithNotification()

        // Extract the result code and data from the intent to start the media projection
        val resultCode = intent.getIntExtra(RESULT_CODE, Activity.RESULT_CANCELED)
        val data: Intent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(DATA, Intent::class.java)
        } else {
            intent.getParcelableExtra(DATA)
        }
        startProjection(resultCode, data)
        return START_NOT_STICKY
    }

    /**
     * Initializes the directory where screenshots will be stored.
     */
    private fun initializeStoreDirectory() {
        val externalFilesDir = getExternalFilesDir(null)
        if (externalFilesDir != null) {
            mStoreDir = "${externalFilesDir.absolutePath}/screenshots/"
            val storeDirectory = File(mStoreDir!!)
            if (!storeDirectory.exists() && !storeDirectory.mkdirs()) {
                Log.e(TAG, "Failed to create file storage directory.")
                stopSelf()
            }
        } else {
            Log.e(TAG, "Failed to create file storage directory, getExternalFilesDir is null.")
            stopSelf()
        }
    }

    /**
     * Starts the foreground service with a notification.
     */
    private fun startForegroundWithNotification() {
        val notification: Pair<Int, Notification> = NotificationUtils.getNotification(this)
        startForeground(notification.first, notification.second)
    }

    /**
     * Starts the media projection for capturing the screen.
     */
    private fun startProjection(resultCode: Int, data: Intent?) {
        val mpManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mMediaProjection = mpManager.getMediaProjection(resultCode, data!!)
        mMediaProjection?.let { mediaProjection ->

            // Register a callback to handle projection stop events
            mediaProjection.registerCallback(object : MediaProjection.Callback() {
                override fun onStop() {
                    super.onStop()
                    releaseResources()
                    stopSelf()
                }
            }, null)

            // Get display metrics for the virtual display
            val metrics = Resources.getSystem().displayMetrics
            mDensity = metrics.densityDpi
            mWidth = metrics.widthPixels
            mHeight = metrics.heightPixels

            // Initialize the ImageReader for capturing the screen
            mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2).apply {
                setOnImageAvailableListener(ImageAvailableListener(), null)
            }

            // Create a virtual display for the media projection after a delay
            serviceScope.launch {
                delay(500) // Delay in milliseconds
                createVirtualDisplay(mediaProjection)
            }
        } ?: run {
            Log.e(TAG, "Failed to start media projection")
            stopSelf()
        }
    }

    /**
     * Creates a virtual display for the media projection.
     */
    private fun createVirtualDisplay(mediaProjection: MediaProjection) {
        mVirtualDisplay = mediaProjection.createVirtualDisplay(
            SCREEN_CAP_NAME,
            mWidth,
            mHeight,
            mDensity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            mImageReader?.surface,
            null,
            null
        )
    }

    /**
     * Releases resources and stops the media projection.
     */
    private fun releaseResources() {
        mVirtualDisplay?.release()
        mImageReader?.close()
        mMediaProjection?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseResources()
        serviceScope.cancel()
    }

    companion object {
        private const val TAG = "ScreenCaptureService"
        private const val RESULT_CODE = "RESULT_CODE"
        private const val DATA = "DATA"
        private const val SCREEN_CAP_NAME = "screen_cap"

        /**
         * Creates an Intent to start the ScreenCaptureService.
         */
        fun getStartIntent(context: Context, resultCode: Int, data: Intent): Intent {
            return Intent(context, ScreenCaptureService::class.java).apply {
                putExtra(RESULT_CODE, resultCode)
                putExtra(DATA, data)
            }
        }
    }
}