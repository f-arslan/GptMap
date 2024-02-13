package com.espressodev.gptmap.core.save_screenshot

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
import java.io.File
import java.io.FileOutputStream

/**
 * Service for capturing the device's screen.
 */
class SaveScreenshotService : Service() {
    private var mMediaProjection: MediaProjection? = null
    private var mStoreDir: String? = null
    private var mImageReader: ImageReader? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    private var mDensity = 0
    private var mWidth = 0
    private var mHeight = 0

    /**
     * Listener for new images available for processing.
     * Only send Finished broadcast when the screenshot is saved.
     * Sometimes due to error, the screenshot is not saved.
     */
    private inner class ImageAvailableListener : ImageReader.OnImageAvailableListener {
        override fun onImageAvailable(reader: ImageReader) {
            var fos: FileOutputStream? = null
            var bitmap: Bitmap? = null
            try {
                val image = reader.acquireLatestImage()
                image?.use {
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        bitmap?.compress(Bitmap.CompressFormat.WEBP_LOSSY, 80, fos!!)
                    } else {
                        bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, fos!!)
                    }
                    Log.d(TAG, "Screenshot captured")
                    sendBroadcast(
                        Intent(ACTION_SERVICE_STOPPED).apply {
                            setPackage(applicationContext.packageName)
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to capture screenshot because: ${e.message}")
            } finally {
                fos?.close()
                bitmap?.recycle()
                stopSelf() // Stop the service after taking the screenshot
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
        startForegroundWithNotification()
        // Extract the result code and data from the intent to start the media projection
        val resultCode = intent.getIntExtra(RESULT_CODE, Activity.RESULT_CANCELED)
        val data: Intent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(DATA, Intent::class.java)
        } else {
            intent.getParcelableExtra(DATA)
        }
        startProjection(resultCode, data)
        sendBroadcast(
            Intent(ACTION_SERVICE_STARTED)
                .apply {
                    setPackage(applicationContext.packageName)
                }
        )
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
            mediaProjection.registerCallback(
                object : MediaProjection.Callback() {
                    override fun onStop() {
                        super.onStop()
                        releaseResources()
                        stopSelf()
                    }
                },
                null
            )

            // Get display metrics for the virtual display
            val metrics = Resources.getSystem().displayMetrics
            mDensity = metrics.densityDpi
            mWidth = metrics.widthPixels
            mHeight = metrics.heightPixels

            // Check if mImageReader is already initialized
            if (mImageReader == null || mImageReader?.width != mWidth || mImageReader?.height != mHeight) {
                mImageReader?.close() // Close the existing instance if not null
                // Initialize the ImageReader for capturing the screen
                mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2).apply {
                    setOnImageAvailableListener(ImageAvailableListener(), null)
                }
            }

            // Create a virtual display for the media projection after a delay
            createOrUpdateVirtualDisplay(mediaProjection)

        } ?: run {
            Log.e(TAG, "Failed to start media projection")
            stopSelf()
        }
    }

    /**
     * Creates a virtual display for the media projection.
     */
    private fun createOrUpdateVirtualDisplay(mediaProjection: MediaProjection) {
        // Release the previous virtual display if it exists
        mVirtualDisplay?.release()

        // Create a new virtual display
        mVirtualDisplay = mediaProjection.createVirtualDisplay(
            "ScreenCapture",
            mWidth,
            mHeight,
            mDensity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            mImageReader?.surface,
            null,
            null
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseResources()
    }

    /**
     * Releases resources and stops the media projection.
     */
    private fun releaseResources() {
        mVirtualDisplay?.release()
        mImageReader?.close()
        mMediaProjection?.stop()
        mImageReader = null
    }

    companion object {
        private const val TAG = "ScreenCaptureService"
        private const val RESULT_CODE = "RESULT_CODE"
        private const val DATA = "DATA"
        const val ACTION_SERVICE_STARTED =
            "com.espressodev.gptmap.core.screen_capture.SERVICE_STARTED"
        const val ACTION_SERVICE_STOPPED =
            "com.espressodev.gptmap.core.screen_capture.SERVICE_STOPPED"

        /**
         * Creates an Intent to start the ScreenCaptureService.
         */
        fun getStartIntent(context: Context, resultCode: Int, data: Intent): Intent {
            return Intent(context, SaveScreenshotService::class.java).apply {
                putExtra(RESULT_CODE, resultCode)
                putExtra(DATA, data)
            }
        }
    }
}
