package com.espressodev.gptmap.feature.image_analysis

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.IconType
import com.espressodev.gptmap.core.designsystem.R.raw as AppRaw
import com.espressodev.gptmap.core.designsystem.component.GmCircularIndicator
import com.espressodev.gptmap.core.designsystem.component.GmTopAppBar
import com.espressodev.gptmap.core.designsystem.component.LoadingAnimation
import com.espressodev.gptmap.core.designsystem.theme.GptmapTheme
import com.espressodev.gptmap.core.model.ImageAnalysis
import com.espressodev.gptmap.core.model.Response
import java.time.LocalDateTime
import com.espressodev.gptmap.core.designsystem.R.drawable as AppDrawable
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@Composable
fun ImageAnalysisRoute(
    imageId: String,
    popUp: () -> Unit,
    viewModel: ImageAnalysisViewModel = hiltViewModel(),
) {
    val imageAnalysis by viewModel.imageAnalysis.collectAsStateWithLifecycle()
    Log.d("ImageAnalysisRoute", "imageAnalysis: $imageAnalysis")

    Scaffold(
        topBar = {
            GmTopAppBar(
                title = AppText.image_analysis,
                icon = IconType.Bitmap(painter = painterResource(id = AppDrawable.analyze)),
                onBackClick = popUp
            )
        },
        bottomBar = {
            AnalysisBottomBar("Hello", {}, {})
        }
    ) {
        when (val result = imageAnalysis) {
            is Response.Failure -> LoadingAnimation(animId = AppRaw.confused_man_404)
            Response.Loading -> GmCircularIndicator()
            is Response.Success -> ImageAnalysisScreen(result.data, modifier = Modifier.padding(it))
        }
    }

    LaunchedEffect(key1 = imageId) {
        if (imageId != "default")
            viewModel.initializeImageAnalysis(imageId)
    }
}

@Composable
fun ImageAnalysisScreen(imageAnalysis: ImageAnalysis, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = AppDrawable.istanbul),
            contentDescription = null,
            modifier = Modifier
                .aspectRatio(1f)
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .weight(3f),
            contentScale = ContentScale.Crop
        )
        Divider()
        TextSection(
            modifier = Modifier
                .fillMaxWidth()
                .weight(7f)
        )
    }
}

@Composable
fun TextSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = "Hello")
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
        Log.d("AnalysisTextField", "value: $value")
        derivedStateOf {
            Log.d("AnalysisTextField", "value.isNotEmpty(): ${value.isNotEmpty()}")
            value.isNotEmpty()
        }
    }

    Row(
        modifier = modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            tonalElevation = 4.dp,
            shape = RoundedCornerShape(24.dp)
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                ),
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
            )
        }
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
            )
        )
    }
}