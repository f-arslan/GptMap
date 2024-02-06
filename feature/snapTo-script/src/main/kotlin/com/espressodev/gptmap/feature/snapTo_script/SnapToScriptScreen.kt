package com.espressodev.gptmap.feature.snapTo_script

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.IconType
import com.espressodev.gptmap.core.designsystem.TextType
import com.espressodev.gptmap.core.designsystem.component.GmTopAppBar
import com.espressodev.gptmap.core.designsystem.theme.GptmapTheme
import com.espressodev.gptmap.core.designsystem.util.rememberKeyboardAsState
import com.espressodev.gptmap.core.model.ImageAnalysis
import com.espressodev.gptmap.core.model.ImageMessage
import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.feature.screenshot_gallery.InputSelector
import com.espressodev.gptmap.feature.screenshot_gallery.SnapToScriptUiEvent
import com.espressodev.gptmap.feature.screenshot_gallery.SnapToScriptUiState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnapToScriptRoute(
    imageId: String,
    popUp: () -> Unit,
    viewModel: SnapToScriptViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = imageId) { viewModel.initializeImageAnalysis(imageId) }
    val uiState by viewModel.snapToScriptUiState.collectAsStateWithLifecycle()
    val imageAnalysis by viewModel.imageAnalysis.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            GmTopAppBar(
                text = TextType.Res(AppText.snapTo_script_title),
                icon = IconType.Vector(GmIcons.ImageSearchDefault),
                onBackClick = popUp
            )
        }
    ) {
        SnapToScriptScreen(
            modifier = Modifier.padding(it),
            uiState = uiState,
            imageAnalysis = imageAnalysis,
            onEvent = viewModel::onEvent,
            onSendClick = { viewModel.onSendClick() }
        )
    }

    BackHandler {
        viewModel.onEvent(SnapToScriptUiEvent.OnReset)
        popUp()
    }
}

@Composable
fun SnapToScriptScreen(
    modifier: Modifier,
    uiState: SnapToScriptUiState,
    imageAnalysis: ImageAnalysis,
    onEvent: (SnapToScriptUiEvent) -> Unit,
    onSendClick: () -> Unit,
) {
    val keyboardState by rememberKeyboardAsState()
    val (keyboardHeight, setKeyboardHeight) = remember { mutableStateOf(300.dp) }
    LaunchedEffect(key1 = keyboardState) {
        if (keyboardState.first)
            setKeyboardHeight(keyboardState.second)
    }



    Box(modifier = modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {
            Messages(imageAnalysis.messages)
            ImageSection(
                imageUrl = imageAnalysis.imageUrl,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        ChatTextSection(
            value = uiState.value,
            keyboardHeight = keyboardHeight,
            inputSelector = uiState.inputSelector,
            onValueChange = { onEvent(SnapToScriptUiEvent.OnValueChanged(it)) },
            onSendClick = onSendClick,
            onMicClick = { onEvent(SnapToScriptUiEvent.OnMicClick) },
            onMicOffClick = { onEvent(SnapToScriptUiEvent.OnMicOffClick) },
            onKeyboardClick = { onEvent(SnapToScriptUiEvent.OnKeyboardClick) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .imePadding()
        )
    }
}

@Composable
fun Messages(messages : List<ImageMessage>) {
    LazyColumn {
        items(messages) {
            UserMessage()
            BotMessage()
        }
    }
}

@Composable
fun BotMessage() {

}

@Composable
fun UserMessage() {

}

@Composable
fun ImageSection(imageUrl: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.size(200.dp),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = stringResource(id = AppText.selected_image),
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ChatTextSection(
    value: String,
    keyboardHeight: Dp,
    inputSelector: InputSelector,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onMicClick: () -> Unit,
    onMicOffClick: () -> Unit,
    onKeyboardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTextFieldEmpty by remember(value) {
        derivedStateOf { value.isBlank() }
    }
    val keyboard = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    LaunchedEffect(key1 = inputSelector) {
        if (inputSelector == InputSelector.Keyboard) {
            focusRequester.requestFocus()
        }
    }

    val permission = rememberPermissionState(permission = android.Manifest.permission.RECORD_AUDIO)
    val (shouldShowDialog, setShouldShowDialog) = remember { mutableStateOf(value = false) }

    LaunchedEffect(key1 = shouldShowDialog) {
        if (shouldShowDialog and !permission.status.isGranted) {
            permission.launchPermissionRequest()
        }
        setShouldShowDialog(false)
    }

    val (textFieldValue) = remember(value) {
        mutableStateOf(
            TextFieldValue(
                value,
                TextRange(value.length)
            )
        )
    }

    Column(modifier = modifier) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = textFieldValue,
                onValueChange = {
                    onValueChange(it.text)
                },
                shape = RoundedCornerShape(24.dp),
                placeholder = { Text(text = stringResource(AppText.message)) },
                trailingIcon = {
                    when {
                        isTextFieldEmpty -> when (inputSelector) {
                            InputSelector.None, InputSelector.Keyboard -> {
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            if (!permission.status.isGranted) {
                                                setShouldShowDialog(true)
                                            } else {
                                                focusManager.clearFocus()
                                                keyboard?.hide()
                                                delay(50L)
                                                onMicClick()
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = GmIcons.MicOutlined,
                                        contentDescription = stringResource(id = AppText.mic)
                                    )
                                }
                            }

                            else -> {}
                        }

                        else -> IconButton(onClick = { onValueChange("") }) {
                            Icon(
                                imageVector = GmIcons.ClearDefault,
                                contentDescription = stringResource(id = AppText.clear)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .onFocusEvent { state ->
                        if (state.isFocused) {
                            onKeyboardClick()
                        }
                    }
            )
            FilledTonalIconButton(
                onClick = onSendClick,
                modifier = Modifier.padding(start = 4.dp),
                enabled = !isTextFieldEmpty
            ) {
                Icon(
                    imageVector = GmIcons.ArrowUpwardDefault,
                    contentDescription = stringResource(id = AppText.send)
                )
            }
        }
        AnimatedVisibility(inputSelector == InputSelector.MicBox) {
            MicBox(
                keyboardHeight = keyboardHeight,
                onClick = {
                    onMicOffClick()
                }
            )
        }
    }
}

@Composable
private fun MicBox(keyboardHeight: Dp, onClick: () -> Unit) {
    var duration by remember { mutableStateOf(Duration.ZERO) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            duration += 1.seconds
        }
    }
    Surface(
        modifier = Modifier
            .height(keyboardHeight)
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer,
        onClick = onClick
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "volume effect")
        val animation = infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000),
                repeatMode = RepeatMode.Reverse
            ),
            label = "animation volume"
        )
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .scale(animation.value)
                    .size(100.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f))
            )
            Text(
                duration.toComponents { minutes, seconds, _ ->
                    val min = minutes.toString().padStart(2, '0')
                    val sec = seconds.toString().padStart(2, '0')
                    "$min:$sec"
                },
                Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp),
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = GmIcons.RecordDefault, contentDescription = null)
                Text(text = stringResource(id = AppText.tap_to_stop_record))
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SnapToScriptPreview() {
    val messages = listOf(
        ImageMessage(
            request = "Hello",
            response = "Hi"
        ),
        ImageMessage(
            request = "How are you?",
            response = "I'm fine"
        ),
        ImageMessage(
            request = "What's your name?",
            response = "I'm GPT-3"
        ),
        ImageMessage(
            request = "What's your name?",
            response = "I'm GPT-3"
        ),
    )

    GptmapTheme {
        Messages(messages = messages)
    }
}