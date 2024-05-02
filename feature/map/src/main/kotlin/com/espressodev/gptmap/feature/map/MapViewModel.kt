package com.espressodev.gptmap.feature.map

import androidx.annotation.MainThread
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.common.LogService
import com.espressodev.gptmap.core.common.snackbar.SnackbarManager
import com.espressodev.gptmap.core.model.di.Dispatcher
import com.espressodev.gptmap.core.model.di.GmDispatchers.IO
import com.espressodev.gptmap.feature.screenshot.ScreenshotServiceHandler
import com.espressodev.gptmap.feature.screenshot.ScreenshotState.FINISHED
import com.espressodev.gptmap.feature.screenshot.ScreenshotState.IDLE
import com.espressodev.gptmap.feature.screenshot.ScreenshotState.STARTED
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.internal.ThreadUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@HiltViewModel
class MapViewModel @Inject constructor(
    private val apiService: ApiService,
    private val repositoryBundle: RepositoryBundle,
    private val dataBundle: DataBundle,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle,
    logService: LogService,
    private val screenshotServiceHandler: ScreenshotServiceHandler,
) : GmViewModel(logService) {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationState = MutableStateFlow<NavigationState>(NavigationState.None)
    val navigationState = _navigationState.asStateFlow()

    private val locationImages
        get() = uiState.value.location.locationImages

    private val favouriteId
        get() = uiState.value.location.favouriteId

    private var initializeCalled = false

    @MainThread
    suspend fun initialize() {
        ThreadUtil.ensureMainThread()
        if (initializeCalled) return
        initializeCalled = true

        viewModelScope.launch {
            launch { getFirstLetterOfUser() }
            launch { observeFavouriteIdFromBackStack() }
            launch { collectScreenshotState() }
            launch { repositoryBundle.userRepository.addIfNewUser() }
        }
    }

    private suspend fun collectScreenshotState() {
        screenshotServiceHandler.screenshotStateFlow.collect { state ->
            when (state) {
                IDLE -> Unit
                STARTED -> Unit
                FINISHED -> {
                    _uiState.update { it.copy(screenshotState = FINISHED) }
                    screenshotServiceHandler.unregisterServiceStateReceiver()
                }
            }
        }
    }

    private suspend fun observeFavouriteIdFromBackStack() {
        savedStateHandle.getStateFlow(FavouriteId, "default")
            .collect {
                if (it != "default") {
                    loadLocationFromFavourite(it)
                }
            }
    }

    fun resetNavigation() {
        _navigationState.update { NavigationState.None }
    }

    fun onEvent(event: MapUiEvent) {
        when (event) {
            is MapUiEvent.OnSearchValueChanged -> _uiState.update { it.copy(searchValue = event.text) }
            is MapUiEvent.OnSearchClick -> onSearchClick()
            is MapUiEvent.OnImageDismiss -> {
                _uiState.update { it.copy(imageGalleryState = ImageGalleryState()) }
            }

            is MapUiEvent.OnImageClick -> _uiState.update {
                it.copy(imageGalleryState = ImageGalleryState(event.pos, true))
            }

            MapUiEvent.OnFavouriteClick -> onFavouriteClick()
            is MapUiEvent.OnStreetViewClick -> {
                onStreetViewClick(event.latLng)
            }

            MapUiEvent.OnExploreWithAiClick ->
                _uiState.update { it.copy(bottomSheetState = MapBottomSheetState.DETAIL_CARD) }

            MapUiEvent.OnDetailSheetBackClick ->
                _uiState.update { it.copy(bottomSheetState = MapBottomSheetState.SMALL_INFORMATION_CARD) }

            MapUiEvent.OnBackClick -> reset()

            MapUiEvent.OnScreenshotProcessStarted -> updateUiBeforeProcess()
            MapUiEvent.OnScreenshotProcessCancelled -> reset()
            MapUiEvent.OnChatAiClick -> onChatAiClick()
            is MapUiEvent.OnExploreWithAiClickFromImage -> onExploreWithAiClick(event.index)
            MapUiEvent.OnProfileClick -> _navigationState.update { NavigationState.NavigateToProfile }
            is MapUiEvent.OnLeftClickInFavourite ->
                onNavigationClickInFavourite(event.favouriteId, false)

            is MapUiEvent.OnRightClickInFavourite ->
                onNavigationClickInFavourite(event.favouriteId, true)
        }
    }

    private fun onNavigationClickInFavourite(favouriteId: String, isNext: Boolean) =
        launchCatching {
            val nextFavourite =
                repositoryBundle.getNextOrPrevFavouriteUseCase(favouriteId, isNext).getOrThrow()
            if (nextFavourite != null) {
                savedStateHandle[FavouriteId] = nextFavourite.id
            }
        }

    private fun onChatAiClick() = launchCatching {
        val latestImageId = repositoryBundle.userRepository.getLatestImageId().getOrThrow()
        if (latestImageId.isBlank()) {
            _navigationState.update { NavigationState.NavigateToGallery }
        } else {
            _navigationState.update { NavigationState.NavigateToSnapToScript(latestImageId) }
        }
    }

    private fun onExploreWithAiClick(index: Int) = launchCatching {
        _uiState.update {
            it.copy(isLoading = true, imageGalleryState = ImageGalleryState())
        }

        val analysisId = locationImages[index].analysisId
        val latestImageId = repositoryBundle.userRepository.getLatestImageId().getOrThrow()
        when {
            analysisId != "" -> {
                resetAfterExamineWithAiClick()
                delay(50L)
                when {
                    latestImageId != analysisId -> {
                        dataBundle.dataStoreService.setLatestImageIdForChat(analysisId)
                        _navigationState.update { NavigationState.NavigateToGallery }
                    }

                    else -> {
                        _navigationState.update { NavigationState.NavigateToSnapToScript(analysisId) }
                    }
                }
            }

            else -> {
                val imageAnalysisId =
                    repositoryBundle.imageAnalysisRepository
                        .turnImageToImageAnalysis(imageUrl = locationImages[index].imageUrl)
                        .getOrThrow()
                resetAfterExamineWithAiClick()
                delay(50L)
                _navigationState.update { NavigationState.NavigateToGallery }

                if (favouriteId.isNotEmpty()) {
                    dataBundle.favouriteRealmRepository.updateImageAnalysisId(
                        favouriteId = favouriteId,
                        messageId = locationImages[index].id,
                        imageAnalysisId = imageAnalysisId
                    ).getOrThrow()
                }
            }
        }
    }

    private fun resetAfterExamineWithAiClick() {
        _uiState.update {
            it.copy(
                bottomSheetState = MapBottomSheetState.BOTTOM_SHEET_HIDDEN,
                isLoading = false,
            )
        }
    }

    private fun onSearchClick() = launchCatching {
        _uiState.update {
            it.copy(componentLoadingState = ComponentLoadingState.MAP)
        }

        apiService.geminiRepository.getLocationInfo(uiState.value.searchValue)
            .onSuccess { (location, _) ->
                _uiState.update {
                    it.copy(
                        location = location,
                        componentLoadingState = ComponentLoadingState.NOTHING,
                        bottomSheetState = MapBottomSheetState.SMALL_INFORMATION_CARD,
                        searchValue = ""
                    )
                }

                location.content.city.also { city ->
                    apiService.unsplashRepository.getTwoPhotos(city)
                        .onSuccess { locationImages ->
                            _uiState.update { it.copy(location = location.copy(locationImages = locationImages)) }
                        }
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        componentLoadingState = ComponentLoadingState.NOTHING
                    )
                }
            }
    }

    private fun onFavouriteClick() = launchCatching {
        uiState.value.location.also { location ->
            _uiState.update { state ->
                state.copy(location = state.location.copy(isAddedToFavourite = false))
            }
            repositoryBundle.favouriteRepository.saveImageForLocation(location)
                .onFailure {
                    _uiState.update { state ->
                        state.copy(location = state.location.copy(isAddedToFavourite = true))
                    }
                    throw it
                }
        }
    }

    fun reset() {
        _uiState.update {
            it.copy(
                isComponentVisible = true,
                screenshotState = IDLE,
                bottomSheetState = MapBottomSheetState.BOTTOM_SHEET_HIDDEN,
                isSearchBarVisible = true
            )
        }
    }

    private fun onStreetViewClick(latLng: Pair<Double, Double>) = launchCatching {
        val isStreetAvailable = withContext(ioDispatcher) {
            MapUtils.fetchStreetViewData(LatLng(latLng.first, latLng.second))
        }
        when (isStreetAvailable) {
            Status.OK -> {
                delay(25L)
                _navigationState.update {
                    NavigationState.NavigateToStreetView(
                        Pair(latLng.first.toFloat(), latLng.second.toFloat())
                    )
                }
            }

            else -> SnackbarManager.showMessage(AppText.street_view_not_available)
        }
    }

    private fun updateUiBeforeProcess() {
        _uiState.update {
            it.copy(
                isComponentVisible = false,
                isSearchBarVisible = false,
                screenshotState = STARTED
            )
        }
        screenshotServiceHandler.registerServiceStateReceiver()
    }

    private fun loadLocationFromFavourite(favouriteId: String) = launchCatching {
        val location = withContext(ioDispatcher) {
            dataBundle.favouriteRealmRepository.getFavourite(favouriteId)
        }.toLocation()

        _uiState.update {
            it.copy(
                location = location,
                bottomSheetState = MapBottomSheetState.SMALL_INFORMATION_CARD,
                isComponentVisible = true,
                isSearchBarVisible = false
            )
        }
    }

    private fun getFirstLetterOfUser() = launchCatching {
        val firstChar = repositoryBundle.userRepository.getUserFirstChar().getOrThrow()
        _uiState.update { it.copy(userFirstChar = firstChar) }
    }

    override fun onCleared() {
        launchCatching {
            screenshotServiceHandler.unregisterServiceStateReceiver()
        }
        super.onCleared()
    }
}
