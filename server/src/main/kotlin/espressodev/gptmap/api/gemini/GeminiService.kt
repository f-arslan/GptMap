package espressodev.gptmap.api.gemini

interface GeminiService {
    suspend fun getLocationInfo(textContent: String): Result<Location>
}