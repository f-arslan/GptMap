package espressodev.gptmap.plugins

import espressodev.gptmap.api.unsplash.UnsplashService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


fun Application.configureRouting() {
    val unsplashService by inject<UnsplashService>()
    routing {
        get("/") {
            unsplashService.getTwoPhotos("london").onSuccess {
                call.respond(it)
            }.onFailure {
                call.respond(it)
            }
        }
    }
}
