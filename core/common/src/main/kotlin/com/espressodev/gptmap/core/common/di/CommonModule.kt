package com.espressodev.gptmap.core.common.di

import com.espressodev.gptmap.core.common.LogService
import com.espressodev.gptmap.core.common.NetworkMonitor
import com.espressodev.gptmap.core.common.SpeechToText
import com.espressodev.gptmap.core.common.impl.LogServiceImpl
import com.espressodev.gptmap.core.common.impl.NetworkMonitorImpl
import com.espressodev.gptmap.core.common.impl.SpeechToTextImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
interface CommonModule {
    @Binds
    fun bindsNetworkMonitor(networkMonitor: NetworkMonitorImpl): NetworkMonitor

    @Binds
    fun provideLogService(impl: LogServiceImpl): LogService

    @Binds
    fun provideSpeechToText(impl: SpeechToTextImpl): SpeechToText
}
