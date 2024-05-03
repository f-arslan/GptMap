@file:OptIn(ExperimentalPermissionsApi::class)

package com.espressodev.gptmap.feature.map

import StreetView
import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
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
import com.espressodev.gptmap.core.designsystem.Constants.BOTTOM_BAR_PADDING
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.IconType
import com.espressodev.gptmap.core.designsystem.component.ExploreWithAiButton
import com.espressodev.gptmap.core.designsystem.component.ExtendedButtonWithText
import com.espressodev.gptmap.core.designsystem.component.GmDraggableButton
import com.espressodev.gptmap.core.designsystem.component.GmProgressIndicator
import com.espressodev.gptmap.core.designsystem.component.GradientOverImage
import com.espressodev.gptmap.core.designsystem.component.LottieAnimationPlaceholder
import com.espressodev.gptmap.core.designsystem.component.MapTextField
import com.espressodev.gptmap.core.designsystem.component.ShimmerImage
import com.espressodev.gptmap.core.designsystem.component.SquareButton
import com.espressodev.gptmap.core.designsystem.theme.GptmapTheme
import com.espressodev.gptmap.core.model.Location
import com.espressodev.gptmap.core.model.unsplash.LocationImage
import com.espressodev.gptmap.core.save_screenshot.composable.SaveScreenshot
import com.espressodev.gptmap.feature.map.ComponentLoadingState.MAP
import com.espressodev.gptmap.feature.map.ComponentLoadingState.MY_LOCATION
import com.espressodev.gptmap.feature.map.MapBottomSheetState.BOTTOM_SHEET_HIDDEN
import com.espressodev.gptmap.feature.map.MapBottomSheetState.DETAIL_CARD
import com.espressodev.gptmap.feature.map.MapBottomSheetState.SMALL_INFORMATION_CARD
import com.espressodev.gptmap.feature.screenshot.ScreenshotState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
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

@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
internal fun MapRoute(
    navigateToStreetView: (Pair<Float, Float>) -> Unit,
    navigateToScreenshot: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToSnapToScript: (String) -> Unit,
    navigateToGallery: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel(),
    myLocationViewModel: MyLocationViewModel = hiltViewModel(),
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.initialize()
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val mapLocationState by myLocationViewModel.mapLocationState.collectAsStateWithLifecycle()
    val navigationState by viewModel.navigationState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = navigationState) {
        fun performNavigation(action: () -> Unit) {
            action()
            viewModel.resetNavigation()
        }

        with(navigationState) {
            when (this) {
                NavigationState.NavigateToGallery -> performNavigation(navigateToGallery)
                NavigationState.NavigateToProfile -> performNavigation(navigateToProfile)
                NavigationState.NavigateToScreenshot -> performNavigation(navigateToScreenshot)
                is NavigationState.NavigateToSnapToScript -> performNavigation {
                    navigateToSnapToScript(this.imageId)
                }

                is NavigationState.NavigateToStreetView -> performNavigation {
                    navigateToStreetView(this.latLng)
                }

                NavigationState.None -> Unit
            }
        }
    }

    /**
     * Uses [rememberUpdatedState] for `onEvent` to prevent unnecessary recompositions by maintaining
     * a stable reference. This method ensures minimal performance impact and efficient UI updates by
     * allowing `onEvent` to adapt dynamically to state changes without causing full screen
     * recompositions, thus optimizing UI responsiveness and performance.
     */
    val onEvent by rememberUpdatedState(
        newValue = { event: MapUiEvent ->
            viewModel.onEvent(event)
        }
    )
    val onLocationEvent by rememberUpdatedState(
        newValue = { event: MapLocationEvent ->
            myLocationViewModel.onEvent(event)
        }
    )
    val googleMapTestTag = "map:MapScreen"

    Scaffold(
        modifier = modifier
            .padding(bottom = BOTTOM_BAR_PADDING)
            .semantics { testTagsAsResourceId = true }
    ) {
        uiState.MapScreen(
            mapLocationState,
            onEvent = { event -> onEvent(event) },
            onLocationEvent = { event -> onLocationEvent(event) },
            modifier = Modifier.testTag(googleMapTestTag)
        )
    }

    LaunchedEffect(uiState.screenshotState) {
        if (uiState.screenshotState == ScreenshotState.FINISHED) {
            navigateToScreenshot()
            viewModel.reset()
        }
    }

    if (uiState.isLoading) {
        GmProgressIndicator()
    }
}

@Composable
private fun MapUiState.MapScreen(
    mapMyLocationState: MapMyLocationState,
    onEvent: (MapUiEvent) -> Unit,
    onLocationEvent: (MapLocationEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (imageGalleryState.shouldShownGallery) {
        ImageGallery(
            initialPage = imageGalleryState.currentIndex,
            images = location.locationImages,
            onDismiss = { onEvent(MapUiEvent.OnImageDismiss) },
            onExploreWithAiClick = { onEvent(MapUiEvent.OnExploreWithAiClickFromImage(it)) }
        )
    }
    Box(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = isComponentVisible,
            modifier = Modifier
                .zIndex(1f)
                .offset(y = 72.dp)
        ) {
            GmDraggableButton(
                icon = IconType.Bitmap(AppDrawable.ai_icon),
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                initialAlignment = Alignment.CenterEnd,
                onClick = { onEvent(MapUiEvent.OnChatAiClick) }
            )
        }
        val googleMapTestTag = "map:SearchBar"
        if (isSearchBarVisible) {
            MapSearchBar(
                value = searchValue,
                userFirstChar = userFirstChar,
                onValueChange = { onEvent(MapUiEvent.OnSearchValueChanged(it)) },
                onSearchClick = { onEvent(MapUiEvent.OnSearchClick) },
                onProfileClick = { onEvent(MapUiEvent.OnProfileClick) },
                modifier = Modifier
                    .zIndex(1f)
                    .align(Alignment.TopCenter)
                    .testTag(googleMapTestTag)
            )
        }
        SaveScreenshot(
            onClick = { onEvent(MapUiEvent.OnScreenshotProcessStarted) },
            onCancelClick = { onEvent(MapUiEvent.OnScreenshotProcessCancelled) },
            isButtonVisible = isComponentVisible
        )

        val componentState = when (mapMyLocationState.componentLoadingState) {
            MY_LOCATION -> mapMyLocationState.componentLoadingState
            else -> componentLoadingState
        }

        LoadingDialog(loadingState = componentState)

        MapCameraSection(mapMyLocationState, onEvent = onEvent, onLocationEvent = onLocationEvent)
        DisplayBottomSheet(
            bottomSheetState = bottomSheetState,
            location = location,
            onEvent = onEvent,
            modifier = Modifier.zIndex(2f)
        )
    }
}

@Composable
private fun BoxScope.DisplayBottomSheet(
    bottomSheetState: MapBottomSheetState,
    location: Location,
    onEvent: (MapUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (bottomSheetState) {
        SMALL_INFORMATION_CARD -> location.content.run {
            SmallInformationCard(
                city = city,
                country = country,
                poeticDesc = toPoeticDescWithDecor(),
                onExploreWithAiClick = { onEvent(MapUiEvent.OnExploreWithAiClick) },
                onBackClick = { onEvent(MapUiEvent.OnBackClick) },
                onPrevClick = { onEvent(MapUiEvent.OnLeftClickInFavourite(location.favouriteId)) },
                onNextClick = { onEvent(MapUiEvent.OnRightClickInFavourite(location.favouriteId)) },
                modifier = modifier
            )
        }

        DETAIL_CARD -> location.DetailSheet(onEvent = onEvent, modifier = modifier)

        BOTTOM_SHEET_HIDDEN -> Unit
    }
}

@Composable
private fun BoxScope.MyCurrentLocationButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    ) {
        val isOnePermissionGranted = it.values.any { permissionState -> permissionState }
        if (isOnePermissionGranted) onClick()
    }

    val (shouldShowDialog, setShouldShowDialog) = remember { mutableStateOf(value = false) }

    LaunchedEffect(shouldShowDialog) {
        if (shouldShowDialog) {
            if (!locationPermissionState.checkIsOnePermissionGranted()) {
                locationPermissionState.launchMultiplePermissionRequest()
            } else {
                onClick()
            }
        }
        setShouldShowDialog(false)
    }

    FilledTonalIconButton(
        onClick = {
            if (locationPermissionState.checkIsOnePermissionGranted()) {
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

fun MultiplePermissionsState.checkIsOnePermissionGranted(): Boolean =
    permissions.any { permissionState -> permissionState.status == PermissionStatus.Granted }

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ImageGallery(
    initialPage: Int,
    images: List<LocationImage>,
    onDismiss: () -> Unit,
    onExploreWithAiClick: (Int) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { 2 }, initialPage = initialPage)
    Dialog(onDismissRequest = onDismiss) {
        HorizontalPager(state = pagerState, pageSpacing = 8.dp) { page ->
            Box(
                Modifier
                    .graphicsLayer {
                        val pageOffset =
                            ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue

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
                FloatingActionButton(
                    onClick = { onExploreWithAiClick(page) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-4).dp, y = 4.dp),
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
                ) {
                    GradientOverImage(painterId = AppDrawable.ai_icon)
                }
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
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MapTextField(
        value = value,
        placeholder = AppText.map_text_field_placeholder,
        userFirstChar = userFirstChar,
        onValueChange = onValueChange,
        onSearchClick = onSearchClick,
        onAvatarClick = onProfileClick,
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(8.dp),
        shape = RoundedCornerShape(36.dp)
    )
}

context(BoxScope)
@Composable
private fun MapUiState.MapCameraSection(
    mapMyLocationState: MapMyLocationState,
    onEvent: (MapUiEvent) -> Unit,
    onLocationEvent: (MapLocationEvent) -> Unit,
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(coordinatesLatLng, 14f)
    }
    var animateCameraState by remember { mutableStateOf(true) }

    LaunchedEffect(mapMyLocationState.myCoordinatesLatLng, animateCameraState) {
        val isLocationFetched = mapMyLocationState.isMyLocationFetched
        val shouldAnimateCamera = animateCameraState || !mapMyLocationState.isFirstTimeFetched

        if (isLocationFetched && shouldAnimateCamera) {
            cameraPositionState.navigateToLocation(mapMyLocationState.myLocationCoordinates.toLatLng())
            if (!mapMyLocationState.isFirstTimeFetched) {
                onLocationEvent(MapLocationEvent.OnWhenNavigateToMyLocation)
            }
            animateCameraState = false
        }
    }

    if (mapMyLocationState.isMyLocationButtonVisible) {
        MyCurrentLocationButton(
            onClick = {
                if (mapMyLocationState.isMyLocationFetched) {
                    animateCameraState = true
                } else {
                    onLocationEvent(MapLocationEvent.OnMyCurrentLocationClick)
                }
            },
            modifier = Modifier.zIndex(1f)
        )
    }

    // TODO: Check is the animate became problem for map loading
    LaunchedEffect(coordinatesLatLng) {
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngZoom(
                coordinatesLatLng,
                12f
            )
        )
    }

    NavigateBetweenLocations()

    AnimatedVisibility(
        visible = isComponentVisible,
        modifier = Modifier.zIndex(1f)
    ) {
        GmDraggableButton(
            icon = IconType.Vector(StreetView),
            initialAlignment = Alignment.CenterStart,
            onClick = { onEvent(MapUiEvent.OnStreetViewClick(cameraPositionState.toLatitudeLongitude())) }
        )
    }

    LocationPin(
        isPinVisible = isMapPinVisible,
        isCameraMoving = cameraPositionState.isMoving
    )



    MapSection(cameraPositionState)
}

@Composable
private fun NavigateBetweenLocations() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(2f)
    ) {

    }
}

private suspend fun CameraPositionState.navigateToLocation(latLng: LatLng) {
    animate(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
}

@Composable
private fun MapSection(cameraPositionState: CameraPositionState) {
    val context = LocalContext.current
    var isMapLoaded by remember { mutableStateOf(value = false) }
    if (!isMapLoaded) {
        LottieAnimationPlaceholder(
            rawRes = AppRaw.transistor_earth,
            modifier = Modifier.zIndex(2f)
        )
    }

    ReportDrawnWhen { isMapLoaded }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(zoomControlsEnabled = false, compassEnabled = false),
        properties = MapProperties(
            mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                context,
                AppRaw.map_style
            ),
        ),
        onMapLoaded = { isMapLoaded = true },
    )
}

@Composable
private fun BoxScope.LoadingDialog(
    loadingState: ComponentLoadingState,
    modifier: Modifier = Modifier,
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
            MY_LOCATION -> AppText.loading_your_location
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
private fun DefaultLoadingAnimation(modifier: Modifier = Modifier) {
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
    modifier: Modifier = Modifier,
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
private fun SmallInformationCard(
    city: String,
    country: String,
    poeticDesc: String,
    onExploreWithAiClick: () -> Unit,
    onBackClick: () -> Unit,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBackClick)
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
            .statusBarsPadding()
    ) {
        SquareButton(
            contentDesc = AppText.back_arrow,
            onClick = onBackClick,
            icon = IconType.Vector(GmIcons.ArrowBackOutlined),
            size = 48.dp
        )

        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ExtendedButtonWithText(
                    textId = AppText.prev,
                    iconType = IconType.Vector(GmIcons.ArrowBackIOSOutlined),
                    onClick = onPrevClick,
                    isTextVisible = false,
                    modifier = Modifier.size(48.dp)
                )
                ExtendedButtonWithText(
                    textId = AppText.next,
                    iconType = IconType.Vector(GmIcons.ArrowForwardIOSOutlined),
                    onClick = onNextClick,
                    swap = true,
                    isTextVisible = false,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
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
                        text = "$city, $country",
                        style = MaterialTheme.typography.headlineSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = poeticDesc,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                        fontStyle = FontStyle.Italic
                    )
                    ExploreWithAiButton(onClick = onExploreWithAiClick)
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MapScreenPreview() {
    GptmapTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            SmallInformationCard(
                city = "Istanbul ",
                country = "Hello",
                poeticDesc = "This is good",
                onExploreWithAiClick = {},
                onBackClick = {},
                {}, {}
            )
        }
    }
}
