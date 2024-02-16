package com.espressodev.gptmap.core.designsystem.util

import android.graphics.Rect
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
import com.espressodev.gptmap.core.designsystem.KeyboardState

@Composable
fun rememberKeyboardAsState(): State<KeyboardState> {
    val keyboardState = remember { mutableStateOf(KeyboardState()) }
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
                keyboardState.value = KeyboardState(isKeyboardOpen, keypadHeightDp)
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }

    return keyboardState
}


