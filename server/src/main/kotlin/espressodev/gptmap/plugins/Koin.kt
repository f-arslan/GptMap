package espressodev.gptmap.plugins

import espressodev.gptmap.api.unsplash.UnsplashService
import espressodev.gptmap.api.unsplash.UnsplashServiceImpl
import espressodev.gptmap.service.token.TokenService
import espressodev.gptmap.service.token.TokenServiceImpl
import io.ktor.server.application.*
import org.koin.core.context.startKoin
import org.koin.dsl.module

val unsplashModule = module {
    single<UnsplashService> { UnsplashServiceImpl() }
}

val tokenModule = module {
    single<TokenService> { TokenServiceImpl() }
}

fun Application.configureKoin() {
    startKoin {
        modules(unsplashModule)
        modules(tokenModule)
    }
}
