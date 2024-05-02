package com.espressodev.gptmap.core.domain

import com.espressodev.gptmap.core.model.Coordinates
import com.espressodev.gptmap.core.model.Favourite
import com.espressodev.gptmap.core.model.di.Dispatcher
import com.espressodev.gptmap.core.model.di.GmDispatchers.IO
import com.espressodev.gptmap.core.mongodb.FavouriteRealmRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class GetNextOrPrevFavouriteUseCase @Inject constructor(
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val favouriteRealmRepository: FavouriteRealmRepository,
) {
    suspend operator fun invoke(favouriteId: String, isNext: Boolean) = withContext(ioDispatcher) {
        runCatching {
            val favourites = favouriteRealmRepository.getFavourites().first()
            val favourite = favourites.firstOrNull { it.id == favouriteId }
            val (leftFavourite, rightFavourite) = favourite?.findClosestFavourites(favourites)
                ?: Pair(null, null)
            if (isNext) rightFavourite else leftFavourite
        }
    }
}

fun Coordinates.haversineDistance(other: Coordinates): Double {
    val earthRadiusKm = 6371.0 // Earth's radius in kilometers
    val deltaLatRadians = Math.toRadians(other.latitude - this.latitude)
    val deltaLonRadians = Math.toRadians(other.longitude - this.longitude)
    val sinHalfDeltaLat = sin(deltaLatRadians / 2)
    val sinHalfDeltaLon = sin(deltaLonRadians / 2)
    val a = sinHalfDeltaLat.pow(2) +
            cos(Math.toRadians(this.latitude)) * cos(Math.toRadians(other.latitude)) *
            sinHalfDeltaLon.pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadiusKm * c
}

fun Favourite.findClosestFavourites(favourites: List<Favourite>): Pair<Favourite?, Favourite?> {
    val currentLocation = content.coordinates
    val sortedFavourites =
        favourites.sortedBy { currentLocation.haversineDistance(it.content.coordinates) }
    val currentIndex = sortedFavourites.indexOfFirst { it.id == id }

    val leftFavourite = sortedFavourites.getOrNull(currentIndex - 1)
    val rightFavourite = sortedFavourites.getOrNull(currentIndex + 1)

    return Pair(leftFavourite, rightFavourite)
}
