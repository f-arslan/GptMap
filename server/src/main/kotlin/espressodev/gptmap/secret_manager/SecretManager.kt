package espressodev.gptmap.secret_manager

import com.google.cloud.secretmanager.v1.AccessSecretVersionRequest
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import com.google.cloud.secretmanager.v1.SecretVersionName


class SecretManager {
    private val projectId = System.getenv("PROJECT_ID") ?: "default_project_id"
    private val unsplashSecretId = System.getenv("UNSPLASH_SECRET_ID") ?: "default_unsplash_secret_id"
    private val geminiSecretId = System.getenv("GEMINI_SECRET_ID") ?: "default_gemini_secret_id"

    lateinit var unsplashApiKey: String
    lateinit var geminiApiKey: String

    init { initialize() }

    @Throws(Exception::class)
    private fun initialize() {
        println("Secret manager initialized")
        SecretManagerServiceClient.create().use { client ->
            val unsplashSecretVersionName = SecretVersionName.of(projectId, unsplashSecretId, "latest")
            val geminiSecretVersionName = SecretVersionName.of(projectId, geminiSecretId, "latest")

            // Access the secret version.
            val unsplashResponse = client.accessSecretVersion(AccessSecretVersionRequest.newBuilder().setName(unsplashSecretVersionName.toString()).build())
            val geminiResponse = client.accessSecretVersion(AccessSecretVersionRequest.newBuilder().setName(geminiSecretVersionName.toString()).build())

            unsplashApiKey = unsplashResponse.payload.data.toStringUtf8()
            geminiApiKey = geminiResponse.payload.data.toStringUtf8()
        }
    }

    companion object {
        val secretManager by lazy { SecretManager() }
    }
}