package com.espressodev.gptmap.core.common.splash_navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class AfterSplashState {
    Idle, Map
}

object SplashNavigationManager {
    private val _splashNavigationState = MutableStateFlow(AfterSplashState.Idle)
    val splashNavigationState get() = _splashNavigationState.asStateFlow()

    fun onUserAlreadySignIn() {
        _splashNavigationState.update { AfterSplashState.Map }
    }

    fun onLogout() {
        _splashNavigationState.update { AfterSplashState.Idle }
    }
}