@file:OptIn(ExperimentalPermissionsApi::class)

package com.espressodev.gptmap.feature.map

import StreetView
import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.IconType
import com.espressodev.gptmap.core.designsystem.component.GmDraggableButton
import com.espressodev.gptmap.core.designsystem.component.LottieAnimationPlaceholder
import com.espressodev.gptmap.core.designsystem.component.MapTextField
import com.espressodev.gptmap.core.designsystem.component.ShimmerImage
import com.espressodev.gptmap.core.designsystem.component.SquareButton
import com.espressodev.gptmap.core.designsystem.theme.GptmapTheme
import com.espressodev.gptmap.core.model.Location
import com.espressodev.gptmap.core.model.chatgpt.Content
import com.espressodev.gptmap.core.model.unsplash.LocationImage
import com.espressodev.gptmap.core.save_screenshot.composable.SaveScreenshot
import com.espressodev.gptmap.feature.map.ComponentLoadingState.MAP
import com.espressodev.gptmap.feature.map.MapBottomSheetState.BOTTOM_SHEET_HIDDEN
import com.espressodev.gptmap.feature.map.MapBottomSheetState.DETAIL_CARD
import com.espressodev.gptmap.feature.map.MapBottomSheetState.SMALL_INFORMATION_CARD
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlin.math.absoluteValue
import com.espressodev.gptmap.core.designsystem.R.drawable as AppDrawable
import com.espressodev.gptmap.core.designsystem.R.raw as AppRaw
import com.espressodev.gptmap.core.designsystem.R.string as AppText
import com.espressodev.gptmap.core.designsystem.Constants.BOTTOM_BAR_PADDING

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MapRoute(
    navigateToStreetView: (Pair<Float, Float>) -> Unit,
    navigateToScreenshot: () -> Unit,
    navigateToProfile: () -> Unit,
    favouriteId: String,
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(modifier = modifier.padding(bottom = BOTTOM_BAR_PADDING)) {
        MapScreen(
            uiState = uiState,
            onEvent = { event ->
                viewModel.onEvent(
                    event = event,
                    navigateToStreetView = navigateToStreetView
                )
            },
            onAvatarClick = navigateToProfile,
        )
    }

    LaunchedEffect(favouriteId) {
        if (favouriteId != "default")
            viewModel.loadLocationFromFavourite(favouriteId)
    }

    LaunchedEffect(uiState.screenshotState) {
        if (uiState.screenshotState == ScreenshotState.FINISHED) {
            viewModel.reset()
            navigateToScreenshot()
        }
    }
}

@Composable
private fun MapScreen(
    uiState: MapUiState,
    onEvent: (MapUiEvent) -> Unit,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DisplayImageGallery(
        imageGalleryState = uiState.imageGalleryState,
        location = uiState.location,
        onDismiss = { onEvent(MapUiEvent.OnImageDismiss) }
    )
    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.searchBarState) {
            MapSearchBar(
                value = uiState.searchValue,
                userFirstChar = uiState.userFirstChar,
                onValueChange = { onEvent(MapUiEvent.OnSearchValueChanged(it)) },
                onSearchClick = { onEvent(MapUiEvent.OnSearchClick) },
                onAvatarClick = onAvatarClick,
                modifier = Modifier
                    .zIndex(1f)
                    .align(Alignment.TopCenter)
            )
        }
        SaveScreenshot(
            onClick = { onEvent(MapUiEvent.OnScreenshotProcessStarted) },
            isButtonVisible = uiState.isScreenshotButtonVisible
        )
        LoadingDialog(uiState.componentLoadingState)
        MapSection(
            isPinVisible = uiState.isLocationPinVisible,
            uiState = uiState,
            onEvent = onEvent
        )
        DisplayBottomSheet(
            bottomSheetState = uiState.bottomSheetState,
            location = uiState.location,
            onEvent = onEvent,
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
    onEvent: (MapUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    when (bottomSheetState) {
        SMALL_INFORMATION_CARD -> SmallInformationCard(
            content = location.content,
            onExploreWithAiClick = { onEvent(MapUiEvent.OnExploreWithAiClick) },
            onBackClick = { onEvent(MapUiEvent.OnBackClick) },
            modifier = modifier
        )

        DETAIL_CARD -> DetailSheet(location = location, onEvent = onEvent, modifier = modifier)

        BOTTOM_SHEET_HIDDEN -> {}
    }
}

@Composable
fun BoxScope.MyCurrentLocationButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val (shouldShowDialog, setShouldShowDialog) = remember { mutableStateOf(value = false) }

    LaunchedEffect(shouldShowDialog) {
        if (shouldShowDialog) {
            if (!locationPermissionState.allPermissionsGranted) {
                locationPermissionState.launchMultiplePermissionRequest()
            } else {
                onClick()
            }
        }
        setShouldShowDialog(false)
    }

    FilledTonalIconButton(
        onClick = {
            if (locationPermissionState.allPermissionsGranted) {
                onClick()
            } else {
                setShouldShowDialog(true)
            }
        },
        modifier = modifier
            .align(Alignment.BottomEnd)
            .padding(8.dp)
    ) {
        Icon(
            imageVector = GmIcons.MyLocationOutlined,
            contentDescription = stringResource(id = AppText.my_location)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ImageGallery(initialPage: Int, images: List<LocationImage>, onDismiss: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 2 }, initialPage = initialPage)
    Dialog(onDismissRequest = onDismiss) {
        HorizontalPager(state = pagerState, pageSpacing = 8.dp) { page ->
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
    modifier: Modifier = Modifier
) {
    MapTextField(
        value = value,
        placeholder = AppText.map_text_field_placeholder,
        userFirstChar = userFirstChar,
        onValueChange = onValueChange,
        onSearchClick = onSearchClick,
        onAvatarClick = onAvatarClick,
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(8.dp),
        shape = RoundedCornerShape(36.dp)
    )
}

@Composable
private fun MapSection(uiState: MapUiState, isPinVisible: Boolean, onEvent: (MapUiEvent) -> Unit) {
    val context = LocalContext.current
    val (isMapLoaded, setMapLoaded) = remember { mutableStateOf(value = false) }
    val mapProperties = remember {
        MapProperties(
            mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                context,
                AppRaw.map_style
            )
        )
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(uiState.coordinatesLatLng, 14f)
    }
    LaunchedEffect(uiState.coordinatesLatLng) {
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngZoom(
                uiState.coordinatesLatLng,
                14f
            )
        )
    }
    LaunchedEffect(uiState.myCurrentLocationState) {
        if (uiState.myCurrentLocationState.first) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    uiState.myCoordinatesLatLng,
                    14f
                )
            )
            onEvent(MapUiEvent.OnUnsetMyCurrentLocationState)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = uiState.isStreetViewButtonVisible,
            modifier = Modifier.zIndex(1f)
        ) {
            GmDraggableButton(
                icon = StreetView,
                initialAlignment = Alignment.CenterStart,
                onClick = { onEvent(MapUiEvent.OnStreetViewClick(cameraPositionState.toLatitudeLongitude())) }
            )
        }
        LocationPin(isPinVisible = isPinVisible, isCameraMoving = cameraPositionState.isMoving)
        LottieAnimationPlaceholder(
            rawRes = AppRaw.transistor_earth,
            visible = !isMapLoaded,
            modifier = Modifier.zIndex(2f)
        )
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = false),
            properties = mapProperties,
            onMapLoaded = { setMapLoaded(true) },
        )
        if (uiState.isMyLocationButtonVisible)
            MyCurrentLocationButton(
                onClick = { onEvent(MapUiEvent.OnMyCurrentLocationClick) },
                modifier = Modifier.zIndex(1f)
            )
    }
}

@Composable
private fun BoxScope.LoadingDialog(
    loadingState: ComponentLoadingState,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = loadingState != ComponentLoadingState.NOTHING,
        modifier = modifier
            .zIndex(1f)
            .align(Alignment.TopCenter)
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(top = 72.dp)
            .padding(horizontal = 32.dp)
    ) {
        val title = when (loadingState) {
            MAP -> AppText.discovering_your_dream_place
            ComponentLoadingState.MY_LOCATION -> AppText.loading_your_location
            ComponentLoadingState.NOTHING -> AppText.not_valid_name
        }
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                DefaultLoadingAnimation()
                Text(
                    text = stringResource(title),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
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
            ),
            label = "lottie animation progress"
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
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBackClick)
    Column(
        modifier = modifier
            .align(Alignment.BottomCenter)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SquareButton(
            icon = IconType.Vector(GmIcons.ArrowBackOutlined),
            contentDesc = AppText.back_arrow,
            onClick = onBackClick,
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
                    shadowElevation = 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    OutlinedButton(
                        onClick = onExploreWithAiClick,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = AppDrawable.sparkling),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
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
