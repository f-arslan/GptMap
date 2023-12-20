package espressodev.gptmap.api.unsplash

import espressodev.gptmap.model.LocationImage
import espressodev.gptmap.model.unsplash.UnsplashResponse
import espressodev.gptmap.secret_manager.SecretManager.Companion.secretManager
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.gson.*
import java.text.DateFormat


class UnsplashServiceImpl : UnsplashService {
    private val client = HttpClient(OkHttp) {
        engine {
            config {
                followRedirects(true)
            }
        }

        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
                setDateFormat(DateFormat.LONG)
            }
        }

        install(HttpTimeout) {
            connectTimeoutMillis = 30000
            requestTimeoutMillis = 30000
            socketTimeoutMillis = 30000
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }

        defaultRequest {
            url(BASE_URL)
            header("Content-Type", "application/json")
            header("Authorization", "Client-ID ${secretManager.unsplashApiKey}")
            header("Accept-Version", "v1")
        }
    }

    override suspend fun getTwoPhotos(query: String): Result<List<LocationImage>> {
        return try {
            val response: UnsplashResponse = client.get("/search/photos") {
                parameter("page", 1)
                parameter("query", query)
                parameter("per_page", 2)
                parameter("orientation", "landscape")
            }.body<UnsplashResponse>()
            Result.success(response.toLocationImageList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        const val BASE_URL = "https://api.unsplash.com"
    }
}