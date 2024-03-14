package espressodev.gptmap.plugins

import espressodev.gptmap.service.token.TokenService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureTokenRouting() {
    val tokenService by inject<TokenService>()
    routing {
        get("/token") {
            call.respondText("Hello World!")
        }
    }
}
