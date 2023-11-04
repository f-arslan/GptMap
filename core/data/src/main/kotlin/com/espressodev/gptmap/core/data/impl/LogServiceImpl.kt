package com.espressodev.gptmap.core.data.impl

import android.util.Log
import com.espressodev.gptmap.core.data.LogService
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class LogServiceImpl @Inject constructor() : LogService {
    override fun logNonFatalCrash(throwable: Throwable) =
        Firebase.crashlytics.log(Log.getStackTraceString(throwable))
}