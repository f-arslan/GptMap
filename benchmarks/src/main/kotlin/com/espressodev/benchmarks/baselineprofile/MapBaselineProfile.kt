package com.espressodev.benchmarks.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import com.espressodev.benchmarks.PACKAGE_NAME
import com.espressodev.benchmarks.map.mapFling
import com.espressodev.benchmarks.map.mapWaitForContent
import org.junit.Rule
import org.junit.Test

class MapBaselineProfile {
    @get:Rule
    val baselineProfile = BaselineProfileRule()

    @Test
    fun generate() = baselineProfile.collect(PACKAGE_NAME) {
        startActivityAndWait()
        mapWaitForContent()
        mapFling()
    }
}
