package com.espressodev.gptmap.core.data

interface LogService {
    fun logNonFatalCrash(throwable: Throwable)
}
