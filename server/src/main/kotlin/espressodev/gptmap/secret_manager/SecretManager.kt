package espressodev.gptmap.secret_manager

import com.google.cloud.secretmanager.v1.AccessSecretVersionRequest
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import com.google.cloud.secretmanager.v1.SecretVersionName


class SecretManager {
    private val projectId = System.getenv("PROJECT_ID") ?: "default_project_id"
    private val unsplashSecretId = System.getenv("UNSPLASH_SECRET_ID") ?: "default_unsplash_secret_id"

    lateinit var unsplashApiKey: String

    init { initialize() }

    @Throws(Exception::class)
    private fun initialize() {
        println("Secret manager initialized")
        SecretManagerServiceClient.create().use { client ->
            val unsplashSecretVersionName = SecretVersionName.of(projectId, unsplashSecretId, "latest")

            // Access the secret version.
            val unsplashResponse = client.accessSecretVersion(AccessSecretVersionRequest.newBuilder().setName(unsplashSecretVersionName.toString()).build())

            unsplashApiKey = unsplashResponse.payload.data.toStringUtf8()
        }
    }

    companion object {
        val secretManager by lazy { SecretManager() }
    }
}
