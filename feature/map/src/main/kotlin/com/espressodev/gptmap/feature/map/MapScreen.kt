package com.espressodev.gptmap.feature.map

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.espressodev.gptmap.core.designsystem.Constants.HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.MAX_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.MEDIUM_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.SMALL_PADDING
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.IconType
import com.espressodev.gptmap.core.designsystem.component.LottieAnimationView
import com.espressodev.gptmap.core.designsystem.component.MapTextField
import com.espressodev.gptmap.core.designsystem.component.ShimmerImage
import com.espressodev.gptmap.core.designsystem.component.SquareButton
import com.espressodev.gptmap.core.designsystem.theme.GptmapTheme
import com.espressodev.gptmap.core.model.Location
import com.espressodev.gptmap.core.model.chatgpt.Content
import com.espressodev.gptmap.core.model.unsplash.LocationImage
import com.espressodev.gptmap.core.save_screenshot.composable.SaveScreenshot
import com.espressodev.gptmap.feature.map.ComponentLoadingState.MAP
import com.espressodev.gptmap.feature.map.ComponentLoadingState.STREET_VIEW
import com.espressodev.gptmap.feature.map.MapBottomSheetState.BOTTOM_SHEET_HIDDEN
import com.espressodev.gptmap.feature.map.MapBottomSheetState.DETAIL_CARD
import com.espressodev.gptmap.feature.map.MapBottomSheetState.SMALL_INFORMATION_CARD
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlin.math.absoluteValue
import com.espressodev.gptmap.core.designsystem.R.drawable as AppDrawable
import com.espressodev.gptmap.core.designsystem.R.raw as AppRaw
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MapRoute(
    navigateToStreetView: (Float, Float) -> Unit,
    navigateToFavourite: () -> Unit,
    navigateToScreenshot: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToScreenshotGallery: () -> Unit,
    favouriteId: String,
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        bottomBar = {
            if (uiState.bottomSearchState) {
                MapSearchBar(
                    value = uiState.searchValue,
                    userFirstChar = 'F',
                    onValueChange = { viewModel.onEvent(MapUiEvent.OnSearchValueChanged(it)) },
                    onSearchClick = { viewModel.onEvent(MapUiEvent.OnSearchClick) },
                    onAvatarClick = {}
                )
            }
        },
        topBar = {
            MapTopButtons(
                onFavouriteClick = navigateToFavourite,
                onScreenshotGalleryClick = navigateToScreenshotGallery,
                onAccountClick = navigateToProfile,
                modifier = modifier
            )
        },
        modifier = modifier
    ) {
        MapScreen(
            uiState = uiState,
            onEvent = { event ->
                viewModel.onEvent(
                    event = event,
                    navigateToStreetView = { latLng ->
                        navigateToStreetView(latLng.first.toFloat(), latLng.second.toFloat())
                    }
                )
            },
            modifier = Modifier.padding(top = it.calculateTopPadding())
        )
    }
    SaveScreenshot(
        onSuccess = {
            navigateToScreenshot()
            viewModel.reset()
        },
        onConfirm = {
            viewModel.onEvent(MapUiEvent.OnScreenshotProcessStarted)
        }
    )

    LaunchedEffect(favouriteId) {
        if (favouriteId != "default")
            viewModel.loadLocationFromFavourite(favouriteId)
    }
}

@Composable
private fun MapScreen(
    uiState: MapUiState,
    onEvent: (MapUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val latLng by remember(uiState.location) {
        derivedStateOf {
            uiState.getCoordinates().let { LatLng(it.first, it.second) }
        }
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLng, 13f)
    }
    AnimateCameraPosition(uiState.getCoordinates(), cameraPositionState)
    DisplayImageGallery(
        imageGalleryState = uiState.imageGalleryState,
        location = uiState.location,
        onDismiss = { onEvent(MapUiEvent.OnImageDismiss) }
    )
    Box(modifier = modifier.fillMaxSize()) {
        LoadingDialog(uiState.componentLoadingState)
        MapSection(
            isPinVisible = uiState.isLocationPinVisible,
            cameraPositionState = cameraPositionState
        )
        DisplayBottomSheet(uiState.bottomSheetState, uiState.location, cameraPositionState, onEvent)
    }
}

@Composable
fun MapTopButtons(
    onFavouriteClick: () -> Unit,
    onScreenshotGalleryClick: () -> Unit,
    onAccountClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        GmIconButtonWithText(
            onClick = onAccountClick,
            icon = IconType.Vector(GmIcons.PersonDefault),
        )
        Text(text = "Gptmap", fontWeight = FontWeight.Medium)
        Row {
            GmIconButtonWithText(
                onClick = onFavouriteClick,
                icon = IconType.Vector(GmIcons.GalleryDefault),
            )
            GmIconButtonWithText(
                onClick = onScreenshotGalleryClick,
                icon = IconType.Vector(GmIcons.ScreenshotDefault),
            )
        }
    }
}

@Composable
fun GmIconButtonWithText(
    onClick: () -> Unit,
    icon: IconType,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp)
    ) {
        when (icon) {
            is IconType.Vector -> Icon(
                imageVector = icon.imageVector,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = color
            )

            is IconType.Bitmap -> Image(
                painterResource(id = icon.painterId),
                null,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}

@Composable
private fun AnimateCameraPosition(
    latLng: Pair<Double, Double>,
    cameraPositionState: CameraPositionState
) {
    LaunchedEffect(latLng) {
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLng(LatLng(latLng.first, latLng.second))
        )
    }
}

@Composable
private fun DisplayImageGallery(
    imageGalleryState: Pair<Int, Boolean>,
    location: Location,
    onDismiss: () -> Unit,
) {
    if (imageGalleryState.second) {
        ImageGallery(
            initialPage = imageGalleryState.first,
            images = location.locationImages,
            onDismiss = onDismiss
        )
    }
}

@Composable
private fun BoxScope.DisplayBottomSheet(
    bottomSheetState: MapBottomSheetState,
    location: Location,
    cameraPositionState: CameraPositionState,
    onEvent: (MapUiEvent) -> Unit
) {
    when (bottomSheetState) {
        SMALL_INFORMATION_CARD -> {
            SmallInformationCard(
                content = location.content,
                onExploreWithAiClick = { onEvent(MapUiEvent.OnExploreWithAiClick) },
                onBackClick = { onEvent(MapUiEvent.OnBackClick) },
                onStreetViewClick = { onEvent(MapUiEvent.OnStreetViewClick(cameraPositionState.toLatitudeLongitude())) }
            )
        }

        DETAIL_CARD -> {
            DetailSheet(
                location,
                onEvent = onEvent,
                onStreetViewClick = { onEvent(MapUiEvent.OnStreetViewClick(cameraPositionState.toLatitudeLongitude())) }
            )
        }

        BOTTOM_SHEET_HIDDEN -> {}
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ImageGallery(initialPage: Int, images: List<LocationImage>, onDismiss: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 2 }, initialPage = initialPage)
    Dialog(onDismissRequest = onDismiss) {
        HorizontalPager(state = pagerState, pageSpacing = MEDIUM_PADDING) { page ->
            Box(
                Modifier
                    .graphicsLayer {
                        val pageOffset = (
                                (pagerState.currentPage - page) + pagerState
                                    .currentPageOffsetFraction
                                ).absoluteValue

                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }
            ) {
                ShimmerImage(
                    imageUrl = images[page].imageUrl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )
                UnsplashBanner(name = images[page].imageAuthor)
            }
        }
    }
}

@Composable
private fun MapSearchBar(
    value: String,
    userFirstChar: Char,
    onValueChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onAvatarClick: () -> Unit,
) {
    MapTextField(
        value = value,
        placeholder = AppText.map_text_field_placeholder,
        userFirstChar = userFirstChar,
        onValueChange = onValueChange,
        onSearchClick = onSearchClick,
        onAvatarClick = onAvatarClick,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(8.dp),
        shape = RoundedCornerShape(36.dp)
    )
}

@Composable
private fun MapSection(isPinVisible: Boolean, cameraPositionState: CameraPositionState) {
    val context = LocalContext.current
    val isSystemInDarkTheme = isSystemInDarkTheme()
    var isMapLoaded by remember { mutableStateOf(value = false) }
    val mapProperties = remember {
        if (isSystemInDarkTheme) {
            MapProperties(
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                    context,
                    AppRaw.dark_map_style
                )
            )
        } else {
            MapProperties()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MapContent(
            isPinVisible = isPinVisible,
            cameraPositionState = cameraPositionState,
            mapProperties = mapProperties
        ) {
            isMapLoaded = true
        }
    }

    if (!isMapLoaded) {
        LottieAnimationView(AppRaw.transistor_earth)
    }
}

@Composable
private fun BoxScope.MapContent(
    isPinVisible: Boolean,
    cameraPositionState: CameraPositionState,
    mapProperties: MapProperties,
    onMapLoaded: () -> Unit
) {
    LocationPin(isPinVisible = isPinVisible, isCameraMoving = cameraPositionState.isMoving)
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(zoomControlsEnabled = false),
        properties = mapProperties,
        onMapLoaded = onMapLoaded
    )
}

@Composable
private fun BoxScope.LoadingDialog(loadingState: ComponentLoadingState) {
    AnimatedVisibility(
        visible = loadingState != ComponentLoadingState.NOTHING,
        modifier = Modifier
            .zIndex(1f)
            .align(Alignment.Center)
    ) {
        val textId: Int =
            when (loadingState) {
                STREET_VIEW -> AppText.street_view_loading_header
                MAP -> AppText.discovering_your_dream_place
                ComponentLoadingState.NOTHING -> AppText.info
            }
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(HIGH_PADDING)
            ) {
                DefaultLoadingAnimation()
                Text(
                    text = stringResource(textId),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun DefaultLoadingAnimation(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(AppRaw.map_loading_anim))
    val progress by animateLottieCompositionAsState(composition)

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier.size(56.dp),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun BoxScope.LocationPin(
    isPinVisible: Boolean,
    isCameraMoving: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isPinVisible,
        modifier = modifier
            .align(Alignment.Center)
            .zIndex(1f)
            .size(112.dp),
    ) {
        val composition by rememberLottieComposition(
            spec = LottieCompositionSpec.RawRes(resId = AppRaw.pin)
        )
        val targetProgress = if (isCameraMoving) 0.25f else 1f
        val progress by animateFloatAsState(
            targetValue = targetProgress,
            animationSpec = tween(
                durationMillis = if (isCameraMoving) 800 else 2000,
                easing = LinearEasing
            ), label = "lottie animation progress"
        )

        LottieAnimation(
            composition = composition,
            progress = { progress },
        )
    }
}

@Composable
fun BoxScope.SmallInformationCard(
    content: Content,
    onExploreWithAiClick: () -> Unit,
    onBackClick: () -> Unit,
    onStreetViewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBackClick)
    Column(
        modifier = modifier
            .align(Alignment.BottomCenter)
            .padding(MEDIUM_PADDING),
        verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING)
    ) {
        SquareButton(
            icon = IconType.Vector(GmIcons.ArrowBackOutlined),
            contentDesc = AppText.back_arrow,
            onClick = onBackClick,
        )
        SquareButton(
            icon = IconType.Bitmap(AppDrawable.street_view),
            contentDesc = AppText.search,
            onClick = onStreetViewClick,
        )

        Surface(
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 8.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${content.city}, ${content.country}",
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = content.toPoeticDescWithDecor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                    fontStyle = FontStyle.Italic
                )
                Surface(
                    shadowElevation = SMALL_PADDING,
                    shape = RoundedCornerShape(HIGH_PADDING),
                    modifier = Modifier.padding(top = MEDIUM_PADDING)
                ) {
                    OutlinedButton(
                        onClick = onExploreWithAiClick,
                        shape = RoundedCornerShape(HIGH_PADDING)
                    ) {
                        Image(
                            painter = painterResource(id = AppDrawable.sparkling),
                            contentDescription = null,
                            modifier = Modifier.size(MAX_PADDING)
                        )
                        Spacer(modifier = Modifier.width(MEDIUM_PADDING))
                        Text(text = stringResource(id = AppText.explore_with_ai))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MapPreview() {
    GptmapTheme {
        MapSearchBar(
            value = "libris",
            userFirstChar = 'F',
            onValueChange = {},
            onSearchClick = {},
            {}
        )
    }
}
