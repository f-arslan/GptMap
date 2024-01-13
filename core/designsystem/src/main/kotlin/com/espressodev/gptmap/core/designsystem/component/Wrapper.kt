package com.espressodev.gptmap.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.espressodev.gptmap.core.designsystem.Constants.HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.MEDIUM_PADDING

@Composable
fun AppWrapper(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit = {}) {
    val scrollState = rememberScrollState()
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(HIGH_PADDING)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier.verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(HIGH_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
}

@Composable
fun HeaderWrapper(modifier: Modifier = Modifier, content: @Composable () -> Unit = {}) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        content()
    }
}
