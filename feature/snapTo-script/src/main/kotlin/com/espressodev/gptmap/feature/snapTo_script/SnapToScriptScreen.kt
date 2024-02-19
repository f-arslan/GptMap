package com.espressodev.gptmap.feature.snapTo_script

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.component.BotImage
import com.espressodev.gptmap.core.designsystem.component.DefaultImage
import com.espressodev.gptmap.core.designsystem.component.DraggableImage
import com.espressodev.gptmap.core.designsystem.component.GalleryView
import com.espressodev.gptmap.core.designsystem.util.rememberKeyboardAsState
import com.espressodev.gptmap.core.model.AiResponseStatus
import com.espressodev.gptmap.core.model.ImageMessage
import com.espressodev.gptmap.core.model.ImageType
import com.espressodev.gptmap.feature.screenshot_gallery.InputSelector
import com.espressodev.gptmap.feature.screenshot_gallery.SnapToScriptUiEvent
import com.espressodev.gptmap.feature.screenshot_gallery.SnapToScriptUiState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@Composable
fun SnapToScriptRoute(
    modifier: Modifier = Modifier,
    viewModel: SnapToScriptViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) { viewModel.initialize() }
    val uiState by viewModel.snapToScriptUiState.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val onEvent by rememberUpdatedState(newValue = viewModel::onEvent)
    Box(modifier = modifier.fillMaxSize()) {
        SnapToScriptScreen(
            uiState = uiState,
            imageType = viewModel.imageType,
            rmsFlow = viewModel.rmsFlow,
            messages = messages,
            onEvent = onEvent
        )
    }

    if (!uiState.isPinned) {
        DraggableImage(
            imageUrl = uiState.imageUrl,
            isScreenshot = viewModel.imageType == ImageType.Screenshot,
            onPinClick = { onEvent(SnapToScriptUiEvent.OnPinClick) },
        )
    }
}

@Composable
fun SnapToScriptScreen(
    uiState: SnapToScriptUiState,
    imageType: ImageType,
    rmsFlow: SharedFlow<Int>,
    messages: List<ImageMessage>,
    onEvent: (SnapToScriptUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardState by rememberKeyboardAsState()
    val (keyboardHeight, setKeyboardHeight) = remember { mutableStateOf(300.dp) }

    LaunchedEffect(key1 = keyboardState) {
        if (keyboardState.isVisible) {
            setKeyboardHeight(keyboardState.keypadHeight)
        }
    }
    Box(modifier = modifier.fillMaxSize()) {
        Messages(
            messages = messages,
            userFirstChar = uiState.userFirstChar,
            imageUrl = uiState.imageUrl,
            imageType = imageType,
            aiResponseStatus = uiState.aiResponseStatus,
            isPinned = uiState.isPinned,
            onTypingEnd = { onEvent(SnapToScriptUiEvent.OnTypingEnd) },
            onPinClick = { onEvent(SnapToScriptUiEvent.OnPinClick) },
        )

        ChatTextSection(
            value = uiState.value,
            isTextFieldEnabled = uiState.isTextFieldEnabled,
            rmsFlow = rmsFlow,
            keyboardHeight = keyboardHeight,
            inputSelector = uiState.inputSelector,
            onValueChange = { onEvent(SnapToScriptUiEvent.OnValueChanged(it)) },
            onSendClick = { onEvent(SnapToScriptUiEvent.OnSendClick) },
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
fun Messages(
    messages: List<ImageMessage>,
    userFirstChar: Char,
    imageUrl: String,
    imageType: ImageType,
    aiResponseStatus: AiResponseStatus,
    isPinned: Boolean,
    onTypingEnd: () -> Unit,
    onPinClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    val biggerImageWidth = if (imageType == ImageType.Screenshot) 275.dp else 350.dp
    val smallWidth = if (imageType == ImageType.Screenshot) 175.dp else 250.dp

    var isFullScreen by remember { mutableStateOf(value = false) }
    if (isFullScreen) {
        GalleryView(
            imageUrl = imageUrl,
            biggerImageWidth = biggerImageWidth,
            onDismiss = {
                isFullScreen = false
            }
        )
    }
    val isListEmpty = messages.isEmpty()
    val keyboardState by rememberKeyboardAsState()

    Box(modifier = modifier) {
        Column {
            if (isPinned) {
                DefaultImage(
                    imageUrl = imageUrl,
                    onPinClick = onPinClick,
                    height = 175.dp,
                    width = smallWidth,
                    onFullScreenClick = { isFullScreen = true },
                )
            }
            if (!isListEmpty) {
                LazyColumn(
                    reverseLayout = true,
                    state = scrollState,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    item {
                        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.ime))
                    }

                    item {
                        Spacer(modifier = Modifier.padding(bottom = 56.dp))
                    }

                    messageList(messages, aiResponseStatus, onTypingEnd, userFirstChar)

                    if (!isPinned) {
                        item {
                            Spacer(Modifier.statusBarsPadding())
                        }
                    }
                }
            }
        }

        if (isListEmpty && !keyboardState.isVisible) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                TypeWriterRepeat(stringResource(id = AppText.snapTo_script_placeholder))
            }
        }

        val jumpThreshold = with(LocalDensity.current) { 64.dp.toPx() }

        val jumpToBottomEnabled by remember {
            derivedStateOf {
                scrollState.firstVisibleItemIndex > 1 ||
                        scrollState.firstVisibleItemScrollOffset > jumpThreshold
            }
        }

        JumpToBottom(
            enabled = jumpToBottomEnabled,
            onClick = {
                scope.launch {
                    scrollState.animateScrollToItem(0)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .imePadding()
        )
    }
}

private fun LazyListScope.messageList(
    messages: List<ImageMessage>,
    aiResponseStatus: AiResponseStatus,
    onTypingEnd: () -> Unit,
    userFirstChar: Char
) {
    itemsIndexed(
        items = messages,
        key = { _, message -> message.id }
    ) { index, imageMessage ->
        Spacer(modifier = Modifier.padding(8.dp))

        BotMessageSection(
            message = imageMessage.response,
            isLastItem = index == 0,
            aiResponseStatus = aiResponseStatus,
            onTypingEnd = onTypingEnd
        )

        Spacer(modifier = Modifier.padding(8.dp))
        UserMessageSection(message = imageMessage.request, firstChar = userFirstChar)
    }
}

@Composable
fun JumpToBottom(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = enabled,
        modifier = modifier.offset(y = (-72).dp),
        enter = fadeIn(animationSpec = tween(400)),
        exit = fadeOut(animationSpec = tween(400))
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp
            )
        ) {
            Icon(imageVector = GmIcons.DoubleDownDefault, contentDescription = null)
        }
    }
}

@Composable
fun UserMessageSection(message: String, firstChar: Char, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .offset(y = 2.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(text = firstChar.toString(), style = MaterialTheme.typography.labelMedium)
        }
        Column {
            Text(
                text = stringResource(id = AppText.you),
                style = MaterialTheme.typography.labelLarge,
                letterSpacing = 0.8.sp
            )
            Text(text = message, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BotMessageSection(
    message: String,
    isLastItem: Boolean,
    aiResponseStatus: AiResponseStatus,
    onTypingEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val clipboardManager = LocalClipboardManager.current
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .combinedClickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onLongClick = {
                    clipboardManager.setText(AnnotatedString(message))
                },
                onClick = {}
            )
    ) {
        BotImage()
        Column {
            Text(
                text = stringResource(id = AppText.gemini),
                style = MaterialTheme.typography.labelLarge,
                letterSpacing = 0.8.sp
            )
            when {
                aiResponseStatus == AiResponseStatus.Success && isLastItem -> {
                    TypeWriter(message = message, onTypingEnd = onTypingEnd)
                }

                aiResponseStatus == AiResponseStatus.Loading && isLastItem -> {
                    PulsingBox(
                        size = 16.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                }

                aiResponseStatus is AiResponseStatus.Error && isLastItem -> {
                    Text(
                        text = aiResponseStatus.t.message ?: "Something went wrong",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                else -> {
                    Text(text = message, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun TypeWriterRepeat(
    baseText: String,
    modifier: Modifier = Modifier,
    delayForwardInMillis: Long = 50L,
    delayBackwardInMillis: Long = 25L,
    delayBetweenForwardAndReverseInMillis: Long = 500L,
    delayInEachInMillis: Long = 1000L,
) {
    val charactersWithAlphas =
        remember { mutableStateListOf<Float>().apply { repeat(baseText.length) { add(0f) } } }

    LaunchedEffect(key1 = Unit) {
        while (true) {
            for (i in baseText.indices) {
                charactersWithAlphas[i] = 1f
                delay(delayForwardInMillis)
            }

            delay(delayBetweenForwardAndReverseInMillis)

            for (i in baseText.indices.reversed()) {
                charactersWithAlphas[i] = 0f
                delay(delayBackwardInMillis)
            }

            delay(delayInEachInMillis)
        }
    }

    Row(modifier.padding(4.dp)) {
        baseText.forEachIndexed { index, char ->
            val alpha by animateFloatAsState(
                targetValue = charactersWithAlphas.getOrElse(index) { 0f },
                animationSpec = tween(durationMillis = 200),
                label = "Character animation"
            )
            Text(
                text = char.toString(),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = alpha),
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                    letterSpacing = -(1).sp,
                ),
            )
        }
    }
}

@Composable
fun TypeWriter(
    message: String,
    onTypingEnd: () -> Unit,
    modifier: Modifier = Modifier,
    typeDelayMillis: Long = 10L,
) {
    var textToDisplay by remember { mutableStateOf("") }
    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(message) {
        textToDisplay = ""
        currentIndex = 0

        while (currentIndex < message.length) {
            textToDisplay += message[currentIndex]
            delay(typeDelayMillis)
            currentIndex++
        }
        if (message.isNotEmpty())
            onTypingEnd()
    }

    Text(
        text = textToDisplay,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}

@Composable
fun ChatTextSection(
    value: String,
    isTextFieldEnabled: Boolean,
    rmsFlow: SharedFlow<Int>,
    keyboardHeight: Dp,
    inputSelector: InputSelector,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onMicClick: () -> Unit,
    onMicOffClick: () -> Unit,
    onKeyboardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier) {
        Column {
            TextFieldSection(
                value = value,
                isTextFieldEnabled = isTextFieldEnabled,
                onValueChange = onValueChange,
                inputSelector = inputSelector,
                onSendClick = onSendClick,
                onMicClick = onMicClick,
                onKeyboardClick = onKeyboardClick
            )
            AnimatedVisibility(inputSelector == InputSelector.MicBox) {
                MicBox(
                    keyboardHeight = keyboardHeight,
                    rmsFlow = rmsFlow,
                    onClick = onMicOffClick
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TextFieldSection(
    value: String,
    isTextFieldEnabled: Boolean,
    onValueChange: (String) -> Unit,
    inputSelector: InputSelector,
    onSendClick: () -> Unit,
    onMicClick: () -> Unit,
    onKeyboardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTextFieldEmpty by remember(value) {
        derivedStateOf { value.isBlank() }
    }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val keyboardState by rememberKeyboardAsState()

    var triggerSendLambda by remember { mutableStateOf(value = false) }

    LaunchedEffect(key1 = triggerSendLambda) {
        if (triggerSendLambda and !keyboardState.isVisible) {
            onSendClick()
            triggerSendLambda = false
        }
    }

    LaunchedEffect(key1 = inputSelector) {
        if (inputSelector == InputSelector.Keyboard) {
            focusRequester.requestFocus()
        }
    }

    val permission = rememberPermissionState(permission = android.Manifest.permission.RECORD_AUDIO)
    val (shouldShowPermissionDialog, setShouldShowPermissionDialog) = remember {
        mutableStateOf(value = false)
    }

    LaunchedEffect(key1 = shouldShowPermissionDialog) {
        if (shouldShowPermissionDialog and !permission.status.isGranted) {
            permission.launchPermissionRequest()
        }
        setShouldShowPermissionDialog(false)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = isTextFieldEnabled,
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            placeholder = { Text(stringResource(id = AppText.message)) },
            trailingIcon = {
                if (isTextFieldEmpty) {
                    if (inputSelector in setOf(InputSelector.None, InputSelector.Keyboard)) {
                        IconButton(
                            onClick = {
                                if (!permission.status.isGranted) {
                                    setShouldShowPermissionDialog(true)
                                } else {
                                    focusManager.clearFocus()
                                    onMicClick()
                                }
                            },
                        ) {
                            Icon(
                                GmIcons.MicOutlined,
                                contentDescription = stringResource(id = AppText.mic)
                            )
                        }
                    }
                } else {
                    IconButton(onClick = { onValueChange("") }) {
                        Icon(
                            GmIcons.ClearDefault,
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

        SendButton(isTextFieldEmpty) {
            triggerSendLambda = true
        }
    }
}

@Composable
private fun SendButton(
    isTextFieldEmpty: Boolean,
    onClick: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    FilledTonalIconButton(
        onClick = {
            keyboardController?.hide()
            onClick()
        },
        modifier = Modifier.padding(start = 8.dp),
        enabled = !isTextFieldEmpty
    ) {
        Icon(GmIcons.ArrowUpwardDefault, contentDescription = stringResource(id = AppText.send))
    }
}

@Composable
fun PulsingBox(size: Dp, color: Color, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "Infinite Pulsing transition")
    val animation = infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "infinite pulsing animation"
    )
    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                scaleX = animation.value
                scaleY = animation.value
            }
            .clip(CircleShape)
            .background(color)

    )
}

@Composable
fun PulsingBoxWithAudio(
    size: Dp,
    color: Color,
    rmsScale: Float,
    modifier: Modifier = Modifier,
) {
    val animationScale by animateFloatAsState(
        targetValue = rmsScale,
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearOutSlowInEasing
        ),
        label = "PulsingBoxWithAudio animation"
    )

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                scaleX = animationScale
                scaleY = animationScale
            }
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun MicBox(keyboardHeight: Dp, rmsFlow: SharedFlow<Int>, onClick: () -> Unit) {
    var duration by remember { mutableStateOf(Duration.ZERO) }
    LaunchedEffect("timer") {
        while (true) {
            delay(1000)
            duration += 1.seconds
        }
    }

    val minRms = 0
    val maxRms = 10

    val rms by rmsFlow.collectAsStateWithLifecycle(initialValue = 0)

    fun mapRmsToScale(rms: Int, minRms: Int, maxRms: Int, minScale: Float, maxScale: Float): Float {
        val clampedRms = rms.coerceIn(minRms, maxRms).toFloat()
        return minScale + (clampedRms - minRms) / (maxRms - minRms) * (maxScale - minScale)
    }

    val scale = mapRmsToScale(rms, minRms, maxRms, 0.5f, 1.5f)

    Surface(
        modifier = Modifier
            .height(keyboardHeight)
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer,
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            PulsingBoxWithAudio(
                size = 100.dp,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
                rmsScale = scale,
                modifier = Modifier.align(Alignment.Center)
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
                Text(
                    text = stringResource(id = AppText.tap_to_stop_record),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}
