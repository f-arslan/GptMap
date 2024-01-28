package com.espressodev.gptmap.core.google.impl

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.espressodev.gptmap.core.google.GoogleLocationService
import com.espressodev.gptmap.core.model.Exceptions.GpsNotEnabledException
import com.espressodev.gptmap.core.model.Exceptions.LocationNullThrowable
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class GoogleLocationServiceImpl @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val context: Context
) : GoogleLocationService {

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    override suspend fun getCurrentLocation(): Flow<Result<Pair<Double, Double>>> = callbackFlow {
        if (!isGpsEnabled()) {
            trySend(Result.failure(GpsNotEnabledException()))
            close()
            return@callbackFlow
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 500L)
            .setMaxUpdates(1)
            .setMinUpdateDistanceMeters(1000f)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { newLocation ->
                    trySend(Result.success(Pair(newLocation.latitude, newLocation.longitude)))
                } ?: trySend(Result.failure(LocationNullThrowable()))

                close()
            }
        }

        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    trySend(Result.success(Pair(location.latitude, location.longitude)))
                    close()
                } ?: run {
                    // If location is null, request a new location update
                    fusedLocationProviderClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        null // We handle on the IO thread
                    ).addOnFailureListener { exception ->
                        trySend(Result.failure(exception))
                        close()
                    }
                }
            }
            .addOnFailureListener { exception ->
                trySend(Result.failure(exception))
                close()
            }

        awaitClose {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun isGpsEnabled(): Boolean {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}
