package com.espressodev.benchmarks.map

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.uiautomator.By
import com.espressodev.benchmarks.PACKAGE_NAME
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class SwipeGoogleMapBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun swipeGoogleMapCompilationNone() = swipeGoogleMap(CompilationMode.None())

    @Test
    fun swipeGoogleMapCompilationBaselineProfile() = swipeGoogleMap(CompilationMode.Partial())

    private fun swipeGoogleMap(compilationMode: CompilationMode) = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = compilationMode,
        iterations = 5,
        startupMode = StartupMode.WARM,
        setupBlock = {
            pressHome()
            startActivityAndWait()
            // We use dot notation because it can skip auth phase
            device.findObject(By.text("Continue with Google"))?.click()
            device.waitForIdle()
        }
    ) {
        mapWaitForContent()
        mapFling()
    }
}
