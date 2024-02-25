package com.espressodev.benchmarks.map

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until

fun MacrobenchmarkScope.mapWaitForContent() {
    device.wait(Until.hasObject(By.text("Search here")), 15_000)
}

fun MacrobenchmarkScope.mapFling() {
    val mapScreen = device.findObject(By.res("map:MapScreen"))
    repeat(20) {
        device.flingElement(mapScreen, Direction.DOWN)
    }
    device.waitForIdle(4000)
    repeat(20) {
        device.flingElement(mapScreen, Direction.LEFT)
    }
    device.waitForIdle(4000)
    repeat(20) {
        device.flingElement(mapScreen, Direction.UP)
    }
    device.waitForIdle(4000)
    repeat(20) {
        device.flingElement(mapScreen, Direction.RIGHT)
    }
}

fun UiDevice.flingElement(element: UiObject2, direction: Direction) {
    // for safe fling
    element.setGestureMargin(displayWidth / 5)
    element.fling(direction)
}
