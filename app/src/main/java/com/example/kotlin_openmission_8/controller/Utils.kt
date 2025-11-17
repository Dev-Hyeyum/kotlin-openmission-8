package com.example.kotlin_openmission_8.controller

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

@RequiresApi(Build.VERSION_CODES.O)
suspend fun captureView(view: View, window: Window): Bitmap = suspendCancellableCoroutine { continuation ->
    val bitmap = Bitmap.createBitmap(
        view.width,
        view.height,
        Bitmap.Config.ARGB_8888
    )

    val locationOfViewInWindow = IntArray(2)
    view.getLocationInWindow(locationOfViewInWindow)

    try {
        PixelCopy.request(
            window,
            Rect(
                locationOfViewInWindow[0],
                locationOfViewInWindow[1],
                locationOfViewInWindow[0] + view.width,
                locationOfViewInWindow[1] + view.height
            ),
            bitmap,
            { copyResult ->
                if (copyResult == PixelCopy.SUCCESS) {
                    continuation.resume(bitmap)
                } else {
                    continuation.resumeWithException(Exception("PixelCopy failed: $copyResult"))
                }
            },
            Handler(Looper.getMainLooper())
        )
    } catch (e: Exception) {
        continuation.resumeWithException(e)
    }
}