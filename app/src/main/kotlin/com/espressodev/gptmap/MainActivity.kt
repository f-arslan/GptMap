package com.espressodev.gptmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.espressodev.gptmap.core.designsystem.theme.GptmapTheme
import com.newrelic.agent.android.NewRelic
import dagger.hilt.android.AndroidEntryPoint
import com.espressodev.gptmap.BuildConfig.NEWRELIC_API_KEY
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        NewRelic.withApplicationToken(NEWRELIC_API_KEY).start(this.applicationContext)
        super.onCreate(savedInstanceState)
        setContent {
            GptmapTheme { GmApp() }
        }
    }
}