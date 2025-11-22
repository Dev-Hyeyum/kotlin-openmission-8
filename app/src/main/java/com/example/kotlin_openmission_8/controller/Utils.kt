package com.example.kotlin_openmission_8.controller

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import android.graphics.Color as AndroidColor // 충돌 방지용 alias
import androidx.core.graphics.createBitmap
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@RequiresApi(Build.VERSION_CODES.O)
suspend fun captureView(view: View, window: Window): Bitmap = suspendCancellableCoroutine { continuation ->
    val bitmap = createBitmap(view.width, view.height)

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

fun createDefaultCanvasBitmap(width: Int, height: Int): Bitmap {
    // 썸네일이므로 작은 크기가 적당합니다. (예: 200x150)
    val bitmap = createBitmap(width, height)
    val canvas = Canvas(bitmap)
    // 흰색으로 채우기
    canvas.drawColor(AndroidColor.WHITE)
    return bitmap
}

@Composable
fun SystemBarsHider() {
    val view = LocalView.current

    // 이 코드가 Activity의 Window 객체를 필요로 하므로, Activity를 찾아옵니다.
    if (view.isInEditMode) return // 프리뷰에서는 실행하지 않음

    // Android Activity의 Window 객체와 InsetsController를 가져옵니다.
    val window = (view.context as Activity).window
    val windowInsetsController = remember(window, view) {
        WindowCompat.getInsetsController(window, view)
    }

    DisposableEffect(Unit) {
        // 1. 시스템 바(상단 상태 바, 하단 내비게이션 바)를 숨깁니다.
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        // 2. ✨ [핵심] 사용자가 화면 가장자리에서 스와이프할 때만 시스템 바가 일시적으로 나타나도록 설정합니다.
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // 3. 컴포저블이 화면에서 사라질 때(예: 앱 종료 또는 화면 전환) 원래 상태로 복구합니다.
        onDispose {
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        }
    }
}