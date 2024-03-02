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

    fun flingMap(direction: Direction, repetitions: Int, delay: Long) {
        repeat(repetitions) {
            device.flingElement(mapScreen, direction)
        }
        device.waitForIdle(delay)
    }

    val directions = listOf(Direction.DOWN, Direction.LEFT, Direction.UP, Direction.RIGHT)
    directions.forEach { direction ->
        flingMap(direction, 5, 100)
    }
}

fun MacrobenchmarkScope.search() {
    val searchBar = device.findObject(By.res("map:SearchBar"))
    val searchBarSearchIcon = device.findObject(By.res("map:searchBarSearchIcon"))
    if (searchBar.isClickable && searchBar.isFocusable) {
        searchBar.click()
        searchBar.text = ""
        searchBar.text = "New York"
    }

    device.waitForIdle()
    searchBarSearchIcon.click()
    device.wait(Until.hasObject(By.text("Explore more with AI")), 10_000)
}

fun UiDevice.flingElement(element: UiObject2, direction: Direction) {
    // for safe fling
    element.setGestureMargin(displayWidth / 5)
    element.fling(direction)
}
