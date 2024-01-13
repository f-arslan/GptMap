package com.espressodev.gptmap.feature.map

import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import com.espressodev.gptmap.feature.map.BuildConfig.MAPS_API_KEY
import com.google.maps.android.compose.CameraPositionState

object MapUtils {
    /**
     * This function will check whether a location is available on StreetView or not.
     *
     * @param latLng The `LatLng` object representing the location for which you want to fetch Street View data.
     * @param source The source of the Street View panorama. It is optional parameter and default value is `Source.DEFAULT`
     *   - `Source.DEFAULT`: Use the default Street View source.
     *   - `Source.OUTDOOR`: Use the outdoor Street View source.
     * @return A Status value specifying if the location is available on Street View or not,
     * whether the used key is a right one, or any other error.
     */
    suspend fun fetchStreetViewData(
        latLng: LatLng,
        source: Source = Source.OUTDOOR
    ): Status {

        val urlString = buildString {
            append("https://maps.googleapis.com/maps/api/streetview/metadata")
            append("?location=${latLng.latitude},${latLng.longitude}")
            append("&key=$MAPS_API_KEY")
            append("&source=${source.value}")
        }

        return withContext(Dispatchers.IO) {
            try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                    val responseString = bufferedReader.use { it.readText() }
                    bufferedReader.close()
                    inputStream.close()
                    deserializeResponse(responseString).status
                } else {
                    throw IOException("HTTP Error: $responseCode")
                }
            } catch (e: IOException) {
                e.printStackTrace()
                throw IOException("Network error: ${e.message}")
            }
        }
    }

    private fun deserializeResponse(responseString: String): ResponseStreetView {
        val jsonObject = JSONObject(responseString)
        val statusString = jsonObject.optString("status")
        val status = Status.valueOf(statusString)
        return ResponseStreetView(status)
    }
}

data class ResponseStreetView(val status: Status)

enum class Status {
    OK,
    ZERO_RESULTS,
    NOT_FOUND,
    REQUEST_DENIED,
    OVER_QUERY_LIMIT,
    INVALID_REQUEST,
    UNKNOWN_ERROR
}

enum class Source(var value: String) {
    DEFAULT("default"),
    OUTDOOR("outdoor");
}

fun CameraPositionState.toLatitudeLongitude(): Pair<Double, Double> =
    position.target.let { Pair(it.latitude, it.longitude) }