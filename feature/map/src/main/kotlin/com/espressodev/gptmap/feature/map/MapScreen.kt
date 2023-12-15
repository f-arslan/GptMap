package com.espressodev.gptmap.feature.map

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.espressodev.gptmap.core.designsystem.Constants.HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.MARKER_SIZE
import com.espressodev.gptmap.core.designsystem.Constants.MEDIUM_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.VERY_HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.component.LoadingAnimation
import com.espressodev.gptmap.core.designsystem.component.MapSearchButton
import com.espressodev.gptmap.core.designsystem.component.MapTextField
import com.espressodev.gptmap.core.model.LoadingState
import com.espressodev.gptmap.core.model.LocationImage
import com.espressodev.gptmap.feature.map.MapBottomState.DETAIL
import com.espressodev.gptmap.feature.map.MapBottomState.SEARCH
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


@Composable
fun MapRoute(
    viewModel: MapViewModel = hiltViewModel(),
    navigateToStreetView: (Float, Float) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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

    AnimatedVisibility(uiState.imageGalleryState.second) {
        ImageGallery(
            initialPage = uiState.imageGalleryState.first,
            images = uiState.location.locationImages,
            onDismiss = { onEvent(MapUiEvent.OnImageDismiss) }
        )
    }


    Column(modifier = Modifier.fillMaxSize()) {
        MapSection(
            cameraPositionState = cameraPositionState,
            modifier = Modifier.weight(1f),
            loadingState = uiState.loadingState,
        )

        when (uiState.bottomState) {
            SEARCH -> {
                MapBottomBar(
                    uiState = uiState,
                    onValueChange = { onEvent(MapUiEvent.OnSearchValueChanged(it)) },
                    onSearchClick = { onEvent(MapUiEvent.OnSearchClick) }
                )
            }

            DETAIL -> DetailSheet(
                uiState.location.content,
                uiState.location.locationImages,
                onEvent = onEvent,
                onStreetViewClick = { onEvent(MapUiEvent.OnStreetViewClick(cameraPositionState.position.target)) }
            )
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
    uiState: MapUiState,
    onValueChange: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    Surface {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(MEDIUM_PADDING)
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
private fun MapSection(
    modifier: Modifier,
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
    Box(modifier = modifier.fillMaxSize()) {
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
                Log.d("MapSection", "onMapLoaded: ")
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
                Text(text = stringResource(R.string.discovering_your_dream_place))
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

        // Use a state to control the animation progress
        var progress by remember { mutableStateOf(0f) }

        // Use another state to control whether the animation should play
        val isPlaying by remember { derivedStateOf { isCameraMoving || progress < 0.2f } }

        // Animate the Lottie composition
        val animatedProgress by animateLottieCompositionAsState(
            composition = composition,
            isPlaying = isPlaying,
            iterations = LottieConstants.IterateForever
        )

        // Update the progress state when the animation is playing
        LaunchedEffect(animatedProgress) {
            progress = animatedProgress
        }

        // When isCameraMoving becomes false, set the progress to 0.2f if it's less than that
        LaunchedEffect(isCameraMoving) {
            if (!isCameraMoving && progress < 0.2f) {
                progress = 0.2f
            }
        }

        // Render the Lottie animation with the current progress
        LottieAnimation(
            modifier = Modifier.size(size = 120.dp),
            composition = composition,
            progress = { progress }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun MapPreview() {
    Box {
        LocationPin(true)
    }
}