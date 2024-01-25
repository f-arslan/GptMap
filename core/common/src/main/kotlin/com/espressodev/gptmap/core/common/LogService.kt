package com.espressodev.gptmap.core.common

interface LogService {
    fun logNonFatalCrash(throwable: Throwable)
}
