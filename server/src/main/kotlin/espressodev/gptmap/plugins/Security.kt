package espressodev.gptmap.plugins

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.ratelimit.*
import kotlin.time.Duration.Companion.seconds

fun Application.configureSecurity() {

    install(RateLimit) {
       global {
           rateLimiter(limit = 10, refillPeriod = 60.seconds)
       }
    }

    routing {
        intercept(ApplicationCallPipeline.Plugins) {
            when (val idToken = call.request.header("Authorization")?.removePrefix("Bearer ")) {
                null -> {
                    call.respond(HttpStatusCode.Unauthorized)
                }
                else -> {
                    try {
                        FirebaseAuth.getInstance().verifyIdToken(idToken)
                    } catch (e: FirebaseAuthException) {
                        call.respond(HttpStatusCode.Unauthorized)
                        finish()
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.InternalServerError)
                        finish()
                    }
                }
            }
        }
    }
}