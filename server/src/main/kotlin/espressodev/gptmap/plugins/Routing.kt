package espressodev.gptmap.plugins

import espressodev.gptmap.api.unsplash.UnsplashService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


fun Application.configureRouting() {
    val unsplashService by inject<UnsplashService>()
    routing {
        route("/photos") {
            get("/{query}") {
                val query = call.parameters["query"] ?: run {
                    call.respondText("Missing query parameter", status = HttpStatusCode.BadRequest)
                    return@get
                }
                unsplashService.getTwoPhotos(query)
                    .onSuccess {
                        call.respond(it)
                    }.onFailure {
                        call.respond(HttpStatusCode.InternalServerError, it.message ?: "Unknown error")
                    }
            }
        }
    }
}
