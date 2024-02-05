package com.espressodev.gptmap.core.designsystem.util

import android.graphics.Rect
import android.util.Log
import android.view.ViewTreeObserver
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun rememberKeyboardAsState(): State<Pair<Boolean, Dp>> {
    val keyboardState = remember { mutableStateOf(Pair(false, 0.dp)) }
    val view = LocalView.current
    val density = LocalDensity.current
    var lastKeypadHeight by remember { mutableIntStateOf(0) }

    DisposableEffect(view) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            // Check if the keypad height has changed since the last time we checked
            if (keypadHeight != lastKeypadHeight) {
                lastKeypadHeight = keypadHeight
                val isKeyboardOpen = keypadHeight > screenHeight * 0.15
                val keypadHeightDp = with(density) { keypadHeight.toDp() }
                keyboardState.value = Pair(isKeyboardOpen, keypadHeightDp)
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }

    return keyboardState
}
