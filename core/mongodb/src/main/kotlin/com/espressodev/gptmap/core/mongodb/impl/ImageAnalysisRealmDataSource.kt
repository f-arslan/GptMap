package com.espressodev.gptmap.core.mongodb.impl

import com.espressodev.gptmap.core.model.ImageAnalysis
import com.espressodev.gptmap.core.model.di.Dispatcher
import com.espressodev.gptmap.core.model.di.GmDispatchers.IO
import com.espressodev.gptmap.core.model.realm.RealmImageAnalysis
import com.espressodev.gptmap.core.model.realm.toImageAnalysis
import com.espressodev.gptmap.core.model.sortByDate
import com.espressodev.gptmap.core.mongodb.ImageAnalysisRealmRepository
import com.espressodev.gptmap.core.mongodb.RealmDataSourceBase
import com.espressodev.gptmap.core.mongodb.module.RealmManager.realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ImageAnalysisRealmDataSource @Inject constructor(@Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher) :
    ImageAnalysisRealmRepository, RealmDataSourceBase() {
    override suspend fun saveImageAnalysis(realmImageAnalysis: RealmImageAnalysis): Result<Unit> =
        performRealmTransaction(ioDispatcher) {
            copyToRealm(
                instance = realmImageAnalysis.apply {
                    userId = realmUserId
                },
                updatePolicy = UpdatePolicy.ALL
            )
        }

    override fun getImageAnalyses(): Flow<List<ImageAnalysis>> =
        realm.query<RealmImageAnalysis>("userId == $0", realmUserId).find().asFlow().map {
            it.list.map { realmImageAnalysis -> realmImageAnalysis.toImageAnalysis() }
        }

    override suspend fun getImageAnalysis(id: String): Result<ImageAnalysis> = runCatching {
        withContext(ioDispatcher) {
            realm.query<RealmImageAnalysis>("userId == $0 AND imageId == $1", realmUserId, id)
                .find()
                .first()
                .toImageAnalysis()
                .sortByDate()
        }
    }

    override suspend fun deleteImageAnalysis(imageAnalysisId: String): Result<Unit> =
        withContext(ioDispatcher) {
            performRealmTransaction(ioDispatcher) {
                val imageAnalysisToDelete: RealmImageAnalysis = query<RealmImageAnalysis>(
                    "userId == $0 AND imageId == $1",
                    realmUserId,
                    imageAnalysisId
                )
                    .find()
                    .first()
                delete(imageAnalysisToDelete)
            }
        }

    override suspend fun deleteImageAnalyses(imageAnalysesIds: Set<String>): Result<Unit> =
        withContext(ioDispatcher) {
            performRealmTransaction(ioDispatcher) {
                val imageAnalysesToDelete: RealmResults<RealmImageAnalysis> =
                    query<RealmImageAnalysis>(
                        "userId == $0 AND imageId IN $1",
                        realmUserId,
                        imageAnalysesIds
                    )
                        .find()
                delete(imageAnalysesToDelete)
            }
        }

    override suspend fun updateImageAnalysisText(
        imageAnalysisId: String,
        text: String
    ): Result<Unit> =
        performRealmTransaction(ioDispatcher) {
            val imageAnalysisToUpdate: RealmImageAnalysis = query<RealmImageAnalysis>(
                "userId == $0 AND imageId == $1",
                realmUserId,
                imageAnalysisId
            )
                .find()
                .first()
            findLatest(imageAnalysisToUpdate)?.let { realmImageAnalysis ->
                realmImageAnalysis.title = text
            }
        }
}
