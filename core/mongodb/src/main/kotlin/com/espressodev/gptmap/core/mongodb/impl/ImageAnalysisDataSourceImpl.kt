package com.espressodev.gptmap.core.mongodb.impl

import com.espressodev.gptmap.core.model.ImageAnalysis
import com.espressodev.gptmap.core.model.realm.RealmImageAnalysis
import com.espressodev.gptmap.core.model.realm.toImageAnalysis
import com.espressodev.gptmap.core.model.sortByDate
import com.espressodev.gptmap.core.mongodb.ImageAnalysisService
import com.espressodev.gptmap.core.mongodb.RealmServiceBase
import com.espressodev.gptmap.core.mongodb.module.RealmModule.realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ImageAnalysisServiceImpl : ImageAnalysisService, RealmServiceBase() {
    override suspend fun saveImageAnalysis(realmImageAnalysis: RealmImageAnalysis): Result<Unit> =
        performRealmTransaction {
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

    override fun getImageAnalysis(id: String): Result<ImageAnalysis> = runCatching {
        realm.query<RealmImageAnalysis>("userId == $0 AND imageId == $1", realmUserId, id)
            .find()
            .first()
            .toImageAnalysis()
            .sortByDate()
    }

    override suspend fun deleteImageAnalysis(imageAnalysisId: String): Result<Unit> =
        performRealmTransaction {
            val imageAnalysisToDelete: RealmImageAnalysis = query<RealmImageAnalysis>(
                "userId == $0 AND imageId == $1",
                realmUserId,
                imageAnalysisId
            )
                .find()
                .first()
            delete(imageAnalysisToDelete)
        }

    override suspend fun deleteImageAnalyses(imageAnalysesIds: Set<String>): Result<Unit> =
        performRealmTransaction {
            val imageAnalysesToDelete: RealmResults<RealmImageAnalysis> =
                query<RealmImageAnalysis>(
                    "userId == $0 AND imageId IN $1",
                    realmUserId,
                    imageAnalysesIds
                )
                    .find()
            delete(imageAnalysesToDelete)
        }

    override suspend fun updateImageAnalysisText(imageAnalysisId: String, text: String): Result<Unit> =
        performRealmTransaction {
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
