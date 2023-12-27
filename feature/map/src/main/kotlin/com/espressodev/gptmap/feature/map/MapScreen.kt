package com.espressodev.gptmap.feature.map

import android.content.res.Resources
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
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
import com.espressodev.gptmap.core.designsystem.Constants.BUTTON_SIZE
import com.espressodev.gptmap.core.designsystem.Constants.HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.MARKER_SIZE
import com.espressodev.gptmap.core.designsystem.Constants.MAX_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.MEDIUM_HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.MEDIUM_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.SMALL_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.VERY_HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.component.LoadingAnimation
import com.espressodev.gptmap.core.designsystem.component.MapSearchButton
import com.espressodev.gptmap.core.designsystem.component.MapTextField
import com.espressodev.gptmap.core.designsystem.component.SquareButton
import com.espressodev.gptmap.core.designsystem.theme.gmColorsPalette
import com.espressodev.gptmap.core.model.Location
import com.espressodev.gptmap.core.model.chatgpt.Content
import com.espressodev.gptmap.core.model.unsplash.LocationImage
import com.espressodev.gptmap.feature.map.ComponentLoadingState.MAP
import com.espressodev.gptmap.feature.map.ComponentLoadingState.STREET_VIEW
import com.espressodev.gptmap.feature.map.MapBottomSheetState.DETAIL_CARD
import com.espressodev.gptmap.feature.map.MapBottomSheetState.NOTHING
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

@Composable
fun MapRoute(
    viewModel: MapViewModel = hiltViewModel(),
    navigateToStreetView: (Float, Float) -> Unit,
    navigateToFavourite: () -> Unit,
    favouriteId: String
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        bottomBar = {
            MapBottomBar(
                uiState = uiState,
                onValueChange = { viewModel.onEvent(MapUiEvent.OnSearchValueChanged(it)) },
                onSearchClick = { viewModel.onEvent(MapUiEvent.OnSearchClick) }
            )
        }
    ) {
        MapScreen(
            uiState = uiState,
            onEvent = { event ->
                viewModel.onEvent(
                    event = event,
                    navigateToStreetView = { latLng ->
                        navigateToStreetView(latLng.latitude.toFloat(), latLng.longitude.toFloat())
                    }
                )
            },
            modifier = Modifier.padding(it),
            navigateToFavourite = navigateToFavourite
        )
    }

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
    navigateToFavourite: () -> Unit,
) {
    val latLng = getLatLngFromLocation(uiState.location)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLng, 12f)
    }
    AnimateCameraPosition(latLng, cameraPositionState)
    DisplayImageGallery(uiState.imageGalleryState, uiState.location, onEvent)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {
        MapTopButtons(
            isPlaying = uiState.isFavouriteButtonPlaying,
            onFavouriteClick = navigateToFavourite,
            onCameraClick = { onEvent(MapUiEvent.OnTakeScreenshotClick) }
        )
        LoadingDialog(uiState.componentLoadingState)
        MapSection(cameraPositionState = cameraPositionState)
        DisplayBottomSheet(uiState.bottomSheetState, uiState.location, cameraPositionState, onEvent)
    }
}


@Composable
fun BoxScope.MapTopButtons(
    isPlaying: Boolean,
    onFavouriteClick: () -> Unit,
    onCameraClick: () -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(AppRaw.favourite_anim))
    val progress by animateLottieCompositionAsState(composition, isPlaying = isPlaying)
    Column(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .zIndex(2f)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FloatingActionButton(
            onClick = onFavouriteClick,
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(56.dp)
            )
        }
        FloatingActionButton(
            onClick = onCameraClick,
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
        ) {
            Icon(
                imageVector = GmIcons.CameraFilled,
                contentDescription = stringResource(id = AppText.camera),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(48.dp)
            )
        }
    }

}

@ReadOnlyComposable
private fun getLatLngFromLocation(location: Location): LatLng {
    return location.content.coordinates.let {
        LatLng(it.latitude, it.longitude)
    }
}

@Composable
private fun AnimateCameraPosition(
    latLng: LatLng,
    cameraPositionState: CameraPositionState
) {
    LaunchedEffect(latLng) {
        cameraPositionState.animate(CameraUpdateFactory.newLatLng(latLng))
    }
}

@Composable
private fun DisplayImageGallery(
    imageGalleryState: Pair<Int, Boolean>,
    location: Location,
    onEvent: (MapUiEvent) -> Unit
) {
    if (imageGalleryState.second) {
        ImageGallery(
            initialPage = imageGalleryState.first,
            images = location.locationImages,
            onDismiss = { onEvent(MapUiEvent.OnImageDismiss) }
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
                onStreetViewClick = { onEvent(MapUiEvent.OnStreetViewClick(cameraPositionState.position.target)) }
            )
        }

        DETAIL_CARD -> {
            DetailSheet(
                location,
                onEvent = onEvent,
                onStreetViewClick = { onEvent(MapUiEvent.OnStreetViewClick(cameraPositionState.position.target)) }
            )
        }

        NOTHING -> {}
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
                ImageCard(
                    image = images[page], modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )
                UnsplashBanner(name = images[page].imageAuthor)
            }
        }
    }
}

@Composable
private fun MapBottomBar(
    uiState: MapUiState,
    onValueChange: (String) -> Unit,
    onSearchClick: () -> Unit,
) {
    if (uiState.bottomSearchState)
        Box(modifier = Modifier.padding(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(brush = MaterialTheme.gmColorsPalette.bottomBarBrush)
                    .padding(
                        horizontal = MEDIUM_PADDING,
                        vertical = MEDIUM_HIGH_PADDING
                    )
            ) {
                MapTextField(
                    value = uiState.searchValue,
                    textFieldEnabledState = uiState.searchTextFieldEnabledState,
                    placeholder = AppText.map_text_field_placeholder,
                    onValueChange = onValueChange,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(MEDIUM_PADDING))
                MapSearchButton(
                    buttonEnabledState = uiState.searchButtonEnabledState,
                    onClick = onSearchClick
                )
            }
        }
}

@Composable
private fun MapSection(cameraPositionState: CameraPositionState) {
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
        MapContent(isMapLoaded, cameraPositionState, mapProperties) {
            isMapLoaded = true
        }
    }
}

@Composable
private fun BoxScope.MapContent(
    isMapLoaded: Boolean,
    cameraPositionState: CameraPositionState,
    mapProperties: MapProperties,
    onMapLoaded: () -> Unit
) {
    if (!isMapLoaded) {
        LoadingAnimation(AppRaw.transistor_earth)
    }
    LocationPin(cameraPositionState.isMoving)
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
            .padding(top = VERY_HIGH_PADDING)
            .zIndex(1f)
            .align(Alignment.TopCenter)
    ) {
        val textId: Int =
            when (loadingState) {
                STREET_VIEW -> AppText.street_view_loading_header
                MAP -> AppText.discovering_your_dream_place
                ComponentLoadingState.NOTHING -> AppText.info
            }
        Surface(
            shape = RoundedCornerShape(HIGH_PADDING),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
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
fun DefaultLoadingAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(AppRaw.map_loading_anim))
    val progress by animateLottieCompositionAsState(composition)

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier.size(BUTTON_SIZE),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun BoxScope.LocationPin(isCameraMoving: Boolean) {
    Column(
        modifier = Modifier
            .align(Alignment.Center)
            .zIndex(1f)
            .size(MARKER_SIZE),
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
    onStreetViewClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(MEDIUM_PADDING),
        verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING)
    ) {
        SquareButton(
            icon = GmIcons.ArrowBackOutlined,
            contentDesc = AppText.back_arrow,
            onClick = onBackClick,
            size = BUTTON_SIZE
        )
        SquareButton(
            iconId = AppDrawable.street_view,
            contentDesc = AppText.search,
            onClick = onStreetViewClick,
            size = BUTTON_SIZE
        )
        Surface(
            shape = RoundedCornerShape(HIGH_PADDING),
            shadowElevation = MEDIUM_PADDING,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(HIGH_PADDING),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SMALL_PADDING)
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
