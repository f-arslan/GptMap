package com.espressodev.gptmap.core.model.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val gmDispatcher: GmDispatchers)

enum class GmDispatchers {
    Default,
    IO,
}
