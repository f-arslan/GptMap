package com.espressodev.gptmap.core.mongodb

import com.espressodev.gptmap.core.model.Favourite
import com.espressodev.gptmap.core.model.ImageAnalysis
import com.espressodev.gptmap.core.model.realm.RealmFavourite
import com.espressodev.gptmap.core.model.realm.RealmImageAnalysis
import com.espressodev.gptmap.core.model.realm.RealmImageMessage
import com.espressodev.gptmap.core.model.realm.RealmUser
import kotlinx.coroutines.flow.Flow

interface RealmSyncService {
    suspend fun saveUser(realmUser: RealmUser): Result<Unit>

    suspend fun saveFavourite(realmFavourite: RealmFavourite): Result<Unit>

    suspend fun saveImageAnalysis(realmImageAnalysis: RealmImageAnalysis): Result<Unit>

    fun isUserInDatabase(): Result<Boolean>

    fun getFavourites(): Flow<List<Favourite>>

    fun getFavourite(id: String): Favourite

    fun getImageAnalyses(): Flow<List<ImageAnalysis>>

    fun getImageAnalysis(id: String): Result<ImageAnalysis>

    suspend fun deleteImageAnalysis(imageAnalysisId: String): Result<Unit>

    suspend fun deleteImageAnalyses(imageAnalysesIds: Set<String>): Result<Unit>

    suspend fun updateImageAnalysisText(imageAnalysisId: String, text: String): Result<Unit>

    suspend fun deleteFavourite(favouriteId: String): Result<Unit>

    suspend fun deleteUser(): Result<Unit>
    suspend fun updateFavouriteText(favouriteId: String, text: String): Result<Unit>

    suspend fun addImageMessageToImageAnalysis(imageAnalysisId: String, message: RealmImageMessage): Result<Unit>
}
