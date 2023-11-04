package com.espressodev.gptmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.espressodev.gptmap.core.designsystem.theme.GptmapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GptmapTheme { GmApp() }
        }
    }
}