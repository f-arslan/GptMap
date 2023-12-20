package espressodev.gptmap.plugins

import espressodev.gptmap.api.unsplash.UnsplashService
import espressodev.gptmap.api.unsplash.UnsplashServiceImpl
import io.ktor.server.application.*
import org.koin.core.context.startKoin
import org.koin.dsl.module

val unsplashModule = module {
    single<UnsplashService> { UnsplashServiceImpl() }
}

fun Application.configureKoin() {
    startKoin {
        modules(unsplashModule)
    }
}