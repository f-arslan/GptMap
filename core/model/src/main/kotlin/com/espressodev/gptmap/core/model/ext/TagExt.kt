package com.espressodev.gptmap.core.model.ext


inline fun <reified T> T.classTag(): String = T::class.java.simpleName