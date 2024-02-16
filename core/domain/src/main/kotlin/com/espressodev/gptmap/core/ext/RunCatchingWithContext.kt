package com.espressodev.gptmap.core.ext

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
suspend fun <T> runCatchingWithContext(
    dispatcher: CoroutineDispatcher,
    block: suspend CoroutineScope.() -> T
): Result<T> = withContext(dispatcher) {
    runCatching {
        block()
    }
}
