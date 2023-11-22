package com.espressodev.gptmap.core.mongodb.impl

import io.realm.kotlin.mongodb.App

object MongoService {
    const val APP_ID = "gptmapapp-odcnu"
    val app = App.create(APP_ID)


}