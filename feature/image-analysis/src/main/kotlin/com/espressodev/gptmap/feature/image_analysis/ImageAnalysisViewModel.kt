package com.espressodev.gptmap.feature.image_analysis

import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.data.LogService
import com.espressodev.gptmap.core.data.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ImageAnalysisViewModel @Inject constructor(logService: LogService) : GmViewModel(logService) {

}