package com.espressodev.gptmap.feature.image_analyses

import androidx.lifecycle.viewModelScope
import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.data.LogService
import com.espressodev.gptmap.core.model.Exceptions
import com.espressodev.gptmap.core.model.ImageAnalysis
import com.espressodev.gptmap.core.model.ImageSummary
import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ScreenshotGalleryViewModel @Inject constructor(
    realmSyncService: RealmSyncService,
    logService: LogService
) : GmViewModel(logService) {
    val imageAnalyses = realmSyncService
        .getImageAnalyses()
        .map<List<ImageAnalysis>, Response<List<ImageSummary>>> {
            Response.Success(it.map { imageAnalysis -> imageAnalysis.toImageAnalysisSummary() })
        }
        .catch {
            Response.Failure(Exceptions.RealmFailedToLoadImageAnalysesException())
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            Response.Loading
        )
}
