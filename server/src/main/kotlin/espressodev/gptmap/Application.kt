package espressodev.gptmap

import espressodev.gptmap.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    configureKoin()
    configureDatabase(environment.config)
    configureFirebase()
    configureSecurity()
    configureSerialization()
    configureUnsplashRouting()
    configureTokenRouting()
}
