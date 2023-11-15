package com.espressodev.gptmap.feature.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espressodev.gptmap.core.designsystem.Constants.MEDIUM_PADDING
import com.espressodev.gptmap.core.designsystem.component.MapSearchButton
import com.espressodev.gptmap.core.designsystem.component.MapTextField
import com.espressodev.gptmap.core.model.Location
import com.espressodev.gptmap.core.model.Response
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.espressodev.gptmap.core.designsystem.R.string as AppText


@Composable
fun MapRoute(viewModel: MapViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MapScreen(
        uiState = uiState,
        onSearchValueChange = viewModel::onSearchValueChange,
        onSearchClick = viewModel::onSearchClick
    )
}

@Composable
private fun MapScreen(
    uiState: MapUiState,
    onSearchValueChange: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    Scaffold(
        bottomBar = {
            MapBottomBar(
                searchValue = uiState.searchValue,
                onValueChange = onSearchValueChange,
                onSearchClick = onSearchClick
            )
        },
    ) {
        Column(modifier = Modifier.padding(it)) {
            MapSection(uiState.location)
        }
    }
}


@Composable
private fun MapBottomBar(
    searchValue: String,
    onValueChange: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    BottomAppBar {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = MEDIUM_PADDING)
        ) {
            MapTextField(
                value = searchValue,
                placeholder = AppText.map_textField_placeholder,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(MEDIUM_PADDING))
            MapSearchButton(onClick = onSearchClick)
        }
    }
}

@Composable
private fun MapSection(location: Response<Location>) {
    val singapore = LatLng(1.35, 103.87)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 10f)
    }
    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(position = singapore),
                title = "Singapore",
                snippet = "Marker in Singapore"
            )
        }
    }
}


@Composable
@Preview(showBackground = true)
fun MapPreview() {
    MapBottomBar(searchValue = "etiam", onValueChange = {}, onSearchClick = {})
}