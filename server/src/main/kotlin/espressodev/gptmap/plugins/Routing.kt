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
            get {
                val query = call.request.queryParameters["query"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, "Missing query parameter")
                    return@get
                }
                unsplashService.getTwoPhotos(query)
                    .onSuccess {
                        call.respond(status = HttpStatusCode.OK, it)
                    }.onFailure {
                        call.respond(HttpStatusCode.InternalServerError, it.message ?: "Unknown error")
                    }
            }
        }
    }
}
