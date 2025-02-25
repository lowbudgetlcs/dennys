package com.lowbudgetlcs.workers

import io.ktor.util.logging.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

/**
 * Orchestrates the creation and launching of [IWorker]s on separate coroutines.
 *
 * This class is deprecated. This functionality will be moved to a factory class.
 */
abstract class AbstractWorker : IWorker {
    private val logger = KtorSimpleLogger("com.lowbudgetlcs.workers.AbstractWorker")

    /**
     * Returns a new instance of this worker.
     *
     * This method is deprecated. Functionality will be moved to a factory class.
     */
    abstract fun createInstance(instanceId: Int): IWorker

    override suspend fun launchInstances(count: Int) = coroutineScope {
        val jobs = List(count) { instanceId ->
            launch {
                val worker = createInstance(instanceId)
                logger.info("Launching instance $instanceId of ${worker::class.simpleName}")
                worker.start()
            }
        }
        jobs.joinAll()
    }
}