package com.espressodev.gptmap.feature.map

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
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
import com.espressodev.gptmap.core.designsystem.Constants.MARKER_SIZE
import com.espressodev.gptmap.core.designsystem.Constants.MEDIUM_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.VERY_HIGH_PADDING
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
import com.espressodev.gptmap.core.designsystem.R.string as AppText
import com.espressodev.gptmap.core.designsystem.R.raw as AppRaw


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

@Composable
fun LoadingAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(AppRaw.transistor_traveling))
    val progress by animateLottieCompositionAsState(composition)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .zIndex(2f), contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.fillMaxSize(0.5f)
        )
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
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (!isMapLoaded) {
            LoadingAnimation()
        }
        LoadingDialog(loadingState)
        LocationPin()
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
private fun BoxScope.LocationPin() {
    IconButton(
        modifier = Modifier
            .align(Alignment.Center)
            .zIndex(1f)
            .size(MARKER_SIZE),
        onClick = {}) {
        Icon(
            painter = painterResource(id = AppDrawable.location_pin),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary
        )
    }
}


@Composable
@Preview(showBackground = true)
fun MapPreview() {
    Box {
        LocationPin()
    }
}