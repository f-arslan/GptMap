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
            val favourite = favourites.firstOrNull { it.favouriteId == favouriteId }
            val (leftFavourite, rightFavourite) = favourite?.findClosestFavourites(favourites)
                ?: Pair(null, null)
            if (isNext) rightFavourite else leftFavourite
        }
    }
}

internal fun Coordinates.haversineDistance(other: Coordinates): Double {
    val earthRadiusKm = 6371.0
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

internal fun Favourite.findClosestFavourites(favourites: List<Favourite>): Pair<Favourite?, Favourite?> {
    val currentLocation = content.coordinates
    val sortedFavourites =
        favourites.sortedBy { currentLocation.haversineDistance(it.content.coordinates) }
            .map { it to it.content.coordinates.directionTo(this.content.coordinates) }

    val directionGroups = sortedFavourites.groupBy { (_, direction) ->
        when (direction) {
            Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.NORTH -> "LEFT"
            Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST -> "RIGHT"
        }
    }

    val leftFavourite =
        directionGroups["RIGHT"]?.firstOrNull { it.first.favouriteId != this.favouriteId }?.first
    val rightFavourite =
        directionGroups["LEFT"]?.firstOrNull { it.first.favouriteId != this.favouriteId }?.first

    return Pair(leftFavourite, rightFavourite)
}

enum class Direction {
    NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST, NORTH
}

internal fun Coordinates.directionTo(other: Coordinates): Direction {
    val lat1 = Math.toRadians(other.latitude)
    val lat2 = Math.toRadians(this.latitude)
    val lon1 = Math.toRadians(other.longitude)
    val lon2 = Math.toRadians(this.longitude)

    val deltaLon = lon2 - lon1

    val y = sin(deltaLon) * cos(lat2)
    val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(deltaLon)
    val bearingRadians = atan2(y, x)
    val bearingDegrees = (Math.toDegrees(bearingRadians) + 360) % 360 // Normalize to 0-360

    return when {
        bearingDegrees >= 22.5 && bearingDegrees < 67.5 -> Direction.NORTH_EAST
        bearingDegrees >= 67.5 && bearingDegrees < 112.5 -> Direction.EAST
        bearingDegrees >= 112.5 && bearingDegrees < 157.5 -> Direction.SOUTH_EAST
        bearingDegrees >= 157.5 && bearingDegrees < 202.5 -> Direction.SOUTH
        bearingDegrees >= 202.5 && bearingDegrees < 247.5 -> Direction.SOUTH_WEST
        bearingDegrees >= 247.5 && bearingDegrees < 292.5 -> Direction.WEST
        bearingDegrees >= 292.5 && bearingDegrees < 337.5 -> Direction.NORTH_WEST
        else -> Direction.NORTH
    }
}
