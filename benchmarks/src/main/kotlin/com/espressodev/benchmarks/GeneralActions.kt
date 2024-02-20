package com.espressodev.benchmarks

import android.Manifest
import android.os.Build
import androidx.benchmark.macro.MacrobenchmarkScope

fun MacrobenchmarkScope.allowWriteToExternal() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val command = "pm grant $packageName ${Manifest.permission.WRITE_EXTERNAL_STORAGE}"
        device.executeShellCommand(command)
    }
}
