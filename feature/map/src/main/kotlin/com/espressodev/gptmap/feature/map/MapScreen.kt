package com.espressodev.gptmap.feature.map

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.espressodev.gptmap.core.designsystem.Constants.HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.MARKER_SIZE
import com.espressodev.gptmap.core.designsystem.Constants.MAX_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.MEDIUM_HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.MEDIUM_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.SMALL_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.VERY_HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.component.LoadingAnimation
import com.espressodev.gptmap.core.designsystem.component.MapSearchButton
import com.espressodev.gptmap.core.designsystem.component.MapTextField
import com.espressodev.gptmap.core.model.LoadingState
import com.espressodev.gptmap.core.model.LocationImage
import com.espressodev.gptmap.core.model.chatgpt.Content
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
import com.google.maps.android.compose.rememberCameraPositionState
import kotlin.math.absoluteValue
import com.espressodev.gptmap.core.designsystem.R.drawable as AppDrawable
import com.espressodev.gptmap.core.designsystem.R.raw as AppRaw
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MapRoute(
    viewModel: MapViewModel = hiltViewModel(),
    navigateToStreetView: (Float, Float) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(bottomBar = {
        MapBottomBar(
            searchValue = uiState.searchValue,
            searchTextFieldEnabledState = uiState.searchTextFieldEnabledState,
            searchButtonEnabledState = uiState.searchButtonEnabledState,
            bottomSearchState = uiState.bottomSearchState,
            onValueChange = { viewModel.onEvent(MapUiEvent.OnSearchValueChanged(it)) },
            onSearchClick = { viewModel.onEvent(MapUiEvent.OnSearchClick) }
        )
    }) {

        MapScreen(
            uiState = uiState,
            onEvent = {
                viewModel.onEvent(
                    event = it,
                    navigateToStreetView = { latLng ->
                        navigateToStreetView(latLng.latitude.toFloat(), latLng.longitude.toFloat())
                    }
                )
            },
        )
    }
}

@Composable
private fun MapScreen(
    uiState: MapUiState,
    onEvent: (MapUiEvent) -> Unit,
) {
    val latLng: LatLng = uiState.location.content.coordinates.let {
        LatLng(it.latitude, it.longitude)
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLng, 10f)
    }
    LaunchedEffect(latLng) {
        if (uiState.location.id != "default")
            cameraPositionState.animate(CameraUpdateFactory.newLatLng(latLng))
    }
    Log.d("MapScreen", uiState.toString())
    AnimatedVisibility(uiState.imageGalleryState.second) {
        ImageGallery(
            initialPage = uiState.imageGalleryState.first,
            images = uiState.location.locationImages,
            onDismiss = { onEvent(MapUiEvent.OnImageDismiss) }
        )
    }

    Box {
        MapSection(
            cameraPositionState = cameraPositionState,
            loadingState = uiState.loadingState,
        )

        when (uiState.bottomSheetState) {
            SMALL_INFORMATION_CARD -> {
                SmallInformationCard(
                    content = uiState.location.content,
                    onExploreWithAiClick = { onEvent(MapUiEvent.OnExploreWithAiClick) }
                )
            }

            DETAIL_CARD -> {
                DetailSheet(
                    uiState.location.content,
                    uiState.location.locationImages,
                    onEvent = onEvent,
                    onStreetViewClick = { onEvent(MapUiEvent.OnStreetViewClick(cameraPositionState.position.target)) }
                )
            }

            NOTHING -> {}
        }
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
                ImageCard(image = images[page])
                UnsplashBanner(name = images[page].imageAuthor)
            }
        }
    }
}

@Composable
private fun MapBottomBar(
    searchValue: String,
    searchTextFieldEnabledState: Boolean,
    searchButtonEnabledState: Boolean,
    bottomSearchState: Boolean,
    onValueChange: (String) -> Unit,
    onSearchClick: () -> Unit,
) {
    if (bottomSearchState)
        BottomAppBar {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(
                    horizontal = MEDIUM_PADDING,
                    vertical = MEDIUM_HIGH_PADDING
                )
            ) {
                MapTextField(
                    value = searchValue,
                    textFieldEnabledState = searchTextFieldEnabledState,
                    placeholder = AppText.map_text_field_placeholder,
                    onValueChange = onValueChange,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(MEDIUM_PADDING))
                MapSearchButton(
                    buttonEnabledState = searchButtonEnabledState,
                    onClick = onSearchClick
                )
            }
        }
}

@Composable
private fun MapSection(
    cameraPositionState: CameraPositionState,
    loadingState: LoadingState,
) {
    val context = LocalContext.current
    val isSystemInDarkTheme = isSystemInDarkTheme()
    var isMapLoaded by remember { mutableStateOf(value = false) }
    val mapProperties by remember(isSystemInDarkTheme) {
        mutableStateOf(
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
        )
    }
    Box(modifier = Modifier.fillMaxSize()) {
        if (!isMapLoaded) {
            LoadingAnimation(AppRaw.transistor_earth)
        }
        LoadingDialog(loadingState)
        LocationPin(cameraPositionState.isMoving)
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            onMapLoaded = {
                isMapLoaded = true
            }
        )
    }
}


@Composable
private fun BoxScope.LoadingDialog(loadingState: LoadingState) {
    AnimatedVisibility(
        visible = loadingState == LoadingState.Loading,
        modifier = Modifier
            .padding(top = VERY_HIGH_PADDING)
            .zIndex(1f)
            .align(Alignment.TopCenter)
    ) {
        Surface(
            shape = RoundedCornerShape(HIGH_PADDING),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING),
                modifier = Modifier.padding(HIGH_PADDING)
            ) {
                CircularProgressIndicator()
                Text(text = stringResource(AppText.discovering_your_dream_place))
            }
        }
    }
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
fun BoxScope.SmallInformationCard(content: Content, onExploreWithAiClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(HIGH_PADDING),
        shadowElevation = MEDIUM_PADDING,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(MEDIUM_PADDING),
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
                style = MaterialTheme.typography.headlineMedium
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




