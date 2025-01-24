package com.lowbudgetlcs.workers

import io.ktor.util.logging.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

abstract class AbstractWorker : Worker {
    private val logger = KtorSimpleLogger("com.lowbudgetlcs.workers.AbstractWorker")

    abstract fun createInstance(instanceId: Int): Worker

    override suspend fun launchInstances(count: Int) = coroutineScope {
        val jobs = List(count) { instanceId ->
            launch {
                val worker = createInstance(instanceId)
                logger.info("Launching instance $instanceId of ${worker::class::simpleName}")
                worker.start()
            }
        }
        jobs.joinAll()
    }
}