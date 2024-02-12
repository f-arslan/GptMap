package com.espressodev.gptmap.feature.screenshot_gallery

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espressodev.gptmap.core.designsystem.Constants.BOTTOM_BAR_PADDING
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.IconType
import com.espressodev.gptmap.core.designsystem.TextType
import com.espressodev.gptmap.core.designsystem.component.GmAlertDialog
import com.espressodev.gptmap.core.designsystem.component.GmEditAlertDialog
import com.espressodev.gptmap.core.designsystem.component.GmTopAppBar
import com.espressodev.gptmap.core.designsystem.component.GradientOverImage
import com.espressodev.gptmap.core.designsystem.component.LottieAnimationPlaceholder
import com.espressodev.gptmap.core.designsystem.component.ShimmerImage
import com.espressodev.gptmap.core.designsystem.component.darkBottomOverlayBrush
import com.espressodev.gptmap.core.model.EditableItemUiEvent
import com.espressodev.gptmap.core.model.ImageSummary
import com.espressodev.gptmap.core.model.Response
import kotlinx.collections.immutable.PersistentSet
import kotlin.math.absoluteValue
import com.espressodev.gptmap.core.designsystem.R.drawable as AppDrawable
import com.espressodev.gptmap.core.designsystem.R.raw as AppRaw
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenshotGalleryRoute(
    popUp: () -> Unit,
    navigateToSnapToScript: (String) -> Unit,
    viewModel: ScreenshotGalleryViewModel = hiltViewModel()
) {
    val imageAnalysesResponse by viewModel.imageAnalyses.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val onEvent by rememberUpdatedState(newValue = viewModel::onEvent)
    val onNavigateToSnapToScript by rememberUpdatedState(
        newValue = { imageId: String, imageUrl: String, navigate: (String) -> Unit ->
            viewModel.navigateToSnapToScript(imageId, imageUrl, navigate)
        }
    )
    Scaffold(
        topBar = {
            GmTopAppBar(
                text = TextType.Res(AppText.screenshot_gallery),
                icon = IconType.Vector(GmIcons.ImageDefault),
                onBackClick = popUp,
                editText = uiState.topBarTitle,
                isInEditMode = uiState.isUiInEditMode,
                selectedItemsCount = uiState.selectedItemsCount,
                onEditClick = { onEvent(EditableItemUiEvent.OnEditClick) },
                onDeleteClick = { onEvent(EditableItemUiEvent.OnDeleteClick) },
                onCancelClick = { onEvent(EditableItemUiEvent.OnCancelClick) }
            )
        },
        modifier = Modifier.padding(bottom = BOTTOM_BAR_PADDING)
    ) {
        when (val result = imageAnalysesResponse) {
            is Response.Failure -> {
                LottieAnimationPlaceholder(AppRaw.confused_man_404)
            }

            Response.Loading -> {}
            is Response.Success -> {
                if (result.data.isNotEmpty()) {
                    ScreenshotGalleryScreen(
                        modifier = Modifier.padding(it),
                        images = result.data,
                        onLongClick = { imageSummary ->
                            onEvent(EditableItemUiEvent.OnLongClickToItem(imageSummary))
                        },
                        selectedItemsIds = uiState.selectedItemsIds,
                        isUiInEditMode = uiState.isUiInEditMode,
                        navigate = { imageId, imageUrl ->
                            onNavigateToSnapToScript(imageId, imageUrl, navigateToSnapToScript)
                        }
                    )
                } else {
                    LottieAnimationPlaceholder(
                        modifier = Modifier.padding(it),
                        rawRes = AppRaw.nothing_here_anim
                    )
                }
            }
        }
    }

    BackHandler {
        if (uiState.isUiInEditMode) {
            onEvent(EditableItemUiEvent.Reset)
        } else {
            popUp()
        }
    }

    if (uiState.editDialogState) {
        GmEditAlertDialog(
            title = AppText.rename,
            textFieldLabel = AppText.screenshot_gallery_edit_dialog_text_field_placeholder,
            onConfirm = { onEvent(EditableItemUiEvent.OnEditDialogConfirm(it)) },
            onDismiss = { onEvent(EditableItemUiEvent.OnEditDialogDismiss) }
        )
    }

    val deleteDialogTitle =
        if (uiState.isSelectedItemAboveOne)
            AppText.screenshot_gallery_multiple_item_delete_dialog_title
        else AppText.screenshot_gallery_delete_dialog_title

    if (uiState.deleteDialogState) {
        GmAlertDialog(
            title = deleteDialogTitle,
            onConfirm = { onEvent(EditableItemUiEvent.OnDeleteDialogConfirm) },
            onDismiss = { onEvent(EditableItemUiEvent.OnDeleteDialogDismiss) }
        )
    }
}

@Composable
fun ScreenshotGalleryScreen(
    images: List<ImageSummary>,
    onLongClick: (ImageSummary) -> Unit,
    selectedItemsIds: PersistentSet<String>,
    navigate: (imageId: String, imageUrl: String) -> Unit,
    modifier: Modifier = Modifier,
    isUiInEditMode: Boolean,
) {
    val (currentPage, setCurrentPage) = rememberSaveable { mutableIntStateOf(0) }
    val (dialogState, setDialogState) = rememberSaveable { mutableStateOf(value = false) }
    val onClickLambda by rememberUpdatedState(
        newValue = { imageSummary: ImageSummary, index: Int ->
            if (isUiInEditMode) {
                onLongClick(imageSummary)
            } else {
                setCurrentPage(index)
                setDialogState(true)
            }
        }
    )
    if (dialogState) {
        GalleryView(
            images = images,
            currentPage = currentPage,
            onDismiss = { setDialogState(false) }
        )
    }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
    ) {
        itemsIndexed(
            images,
            key = { _, imageSummary -> imageSummary.id }
        ) { index, imageSummary ->
            ImageCard(
                imageSummary = imageSummary,
                onClick = { onClickLambda(imageSummary, index) },
                onLongClick = { onLongClick(imageSummary) },
                isSelected = selectedItemsIds.contains(imageSummary.id),
                isUiInEditMode = isUiInEditMode,
                exploreWithAiClick = { navigate(imageSummary.id, imageSummary.imageUrl) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCard(
    imageSummary: ImageSummary,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isUiInEditMode: Boolean = false,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    exploreWithAiClick: () -> Unit = {}
) {
    val (isImageLoaded, setImageLoaded) = remember { mutableStateOf(value = false) }
    val borderStroke = if (isSelected) 3.dp else 0.dp
    val elevation = if (isSelected) 8.dp else 0.dp
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    Box(
        modifier = modifier
            .shadow(elevation)
            .border(borderStroke, borderColor)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
    ) {
        ShimmerImage(
            imageSummary.imageUrl,
            modifier = Modifier.aspectRatio(1f),
            onSuccess = { setImageLoaded(true) },
        )
        if (isImageLoaded) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(brush = darkBottomOverlayBrush)
            )
        }
        Text(
            text = imageSummary.title,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .basicMarquee(iterations = Int.MAX_VALUE),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = Color.White
        )
        if (!isUiInEditMode) {
            FilledTonalIconButton(
                onClick = exploreWithAiClick,
                modifier = Modifier.align(Alignment.TopEnd),
            ) {
                GradientOverImage(painterId = AppDrawable.ai_icon, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GalleryView(
    images: List<ImageSummary>,
    currentPage: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(initialPage = currentPage, pageCount = { images.size })
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(16.dp)
            ) { page ->
                Surface(
                    Modifier
                        .graphicsLayer {
                            val pageOffset =
                                (pagerState.currentPage - page + pagerState.currentPageOffsetFraction)
                                    .absoluteValue

                            val scale = lerp(
                                start = 0.7f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                            scaleX = scale
                            scaleY = scale

                            alpha = lerp(
                                start = 0.5f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                        }
                        .aspectRatio(1f)
                ) {
                    ImageCard(imageSummary = images[page])
                }
            }
            DotsIndicator(
                totalDots = images.size,
                selectedIndex = pagerState.currentPage,
                maxDots = minOf(images.size, 5)
            )
        }
    }
}

@Composable
fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    activeDotColor: Color = Color.White,
    inactiveDotColor: Color = Color.LightGray,
    spacing: Dp = 8.dp,
    selectedDotSize: Dp = 16.dp,
    maxDots: Int = 5
) {
    val listState = rememberLazyListState()
    val totalWidth: Dp = selectedDotSize * maxDots + spacing * (maxDots - 1)
    val widthInPx = with(LocalDensity.current) { selectedDotSize.toPx() }

    LaunchedEffect(key1 = selectedIndex) {
        val viewportSize = listState.layoutInfo.viewportSize
        listState.animateScrollToItem(
            selectedIndex,
            (widthInPx / 2 - viewportSize.width / 2).toInt()
        )
    }

    LazyRow(
        modifier = modifier.width(totalWidth),
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically,
        userScrollEnabled = false
    ) {
        items(totalDots) { index ->
            val scale = animateFloatAsState(
                targetValue = when (index) {
                    selectedIndex -> 1f
                    selectedIndex - 1, selectedIndex + 1 -> 0.70f
                    else -> 0.4f
                },
                animationSpec = tween(
                    durationMillis = 500,
                    easing = FastOutSlowInEasing
                ),
                label = "Dot sizing animation"
            )
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                    }
                    .size(selectedDotSize)
                    .clip(CircleShape)
                    .background(
                        color = if (index == selectedIndex) activeDotColor else inactiveDotColor
                    )
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ImageCardPreview() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        ImageCard(
            imageSummary = ImageSummary(
                id = "1",
                title = "Title",
                imageUrl = "https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png"
            )
        )
    }
}
