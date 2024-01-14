package com.espressodev.gptmap.feature.image_analysis

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.IconType
import com.espressodev.gptmap.core.designsystem.TextType
import com.espressodev.gptmap.core.designsystem.component.GmTopAppBar
import com.espressodev.gptmap.core.designsystem.component.LoadingAnimation
import com.espressodev.gptmap.core.designsystem.theme.GptmapTheme
import com.espressodev.gptmap.core.model.ImageAnalysis
import com.espressodev.gptmap.core.model.Response
import java.time.LocalDateTime
import com.espressodev.gptmap.core.designsystem.R.drawable as AppDrawable
import com.espressodev.gptmap.core.designsystem.R.raw as AppRaw
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@Composable
fun ImageAnalysisRoute(
    imageId: String,
    popUp: () -> Unit,
    viewModel: ImageAnalysisViewModel = hiltViewModel(),
) {
    val imageAnalysis by viewModel.imageAnalysis.collectAsStateWithLifecycle()
    Log.d("ImageAnalysisRoute", "imageAnalysis: $imageAnalysis")
    var value by remember { mutableStateOf("") }
    when (val result = imageAnalysis) {
        is Response.Failure -> LoadingAnimation(animId = AppRaw.confused_man_404)
        Response.Loading -> ImageAnalysisScreen(
            imageAnalysis = ImageAnalysis(title = "Istanbul", date = LocalDateTime.now()),
            value = value,
            onValueChange = { value = it },
            popUp = popUp
        )

        is Response.Success -> ImageAnalysisScreen(
            imageAnalysis = result.data,
            {},
            "",
            {},
        )
    }

    LaunchedEffect(key1 = imageId) {
        if (imageId != "default")
            viewModel.initializeImageAnalysis(imageId)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageAnalysisScreen(
    imageAnalysis: ImageAnalysis,
    popUp: () -> Unit,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    Scaffold(
        topBar = {
            GmTopAppBar(
                textType = TextType.Text(imageAnalysis.title),
                icon = IconType.Bitmap(AppDrawable.analyze),
                onBackClick = popUp,
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars)
            .exclude(WindowInsets.ime),
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            LazyColumn(
                modifier = Modifier.padding(bottom = 64.dp),
            ) {
                imageView()
                items(imageAnalysis.messages, key = { key -> key.hashCode() }) { message ->
                    Text(text = message.response)
                    Divider()
                }
            }
            AnalysisBottomBar(
                value = value,
                onValueChange = onValueChange,
                onClick = {},
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

private fun LazyListScope.imageView() {
    item {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = AppDrawable.istanbul),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .aspectRatio(1f)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun AnalysisBottomBar(
    value: String,
    onValueChange: (String) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val showClearIcon by remember(value) {
        derivedStateOf { value.isNotEmpty() }
    }

    Row(
        modifier = modifier
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            shape = RoundedCornerShape(24.dp),
            placeholder = { Text(text = stringResource(id = AppText.message)) },
            trailingIcon = {
                if (showClearIcon) {
                    IconButton(
                        onClick = { onValueChange("") }
                    ) {
                        Icon(GmIcons.CancelOutlined, null)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        )
        FilledIconButton(onClick = onClick) {
            Icon(
                imageVector = GmIcons.SendDefault,
                contentDescription = stringResource(id = AppText.send)
            )

        }
    }
}

@Composable
@Preview(showBackground = true)
private fun ImageAnalysisPreview() {
    GptmapTheme {
        ImageAnalysisScreen(
            imageAnalysis = ImageAnalysis(
                id = "noluisse",
                imageId = "signiferumque",
                userId = "dictum",
                imageUrl = "https://duckduckgo.com/?q=eu",
                title = "maluisset",
                messages = listOf(),
                date = LocalDateTime.now()
            ),
            {},
            "",
            {}
        )
    }
}