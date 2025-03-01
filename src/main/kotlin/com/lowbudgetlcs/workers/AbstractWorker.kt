package com.lowbudgetlcs.workers

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Orchestrates the creation and launching of [IWorker]s on separate coroutines.
 *
 * This class is deprecated. This functionality will be moved to a factory class.
 */
abstract class AbstractWorker : IWorker {
    private val logger : Logger = LoggerFactory.getLogger(AbstractWorker::class.java)

    /**
     * Returns a new instance of this worker.
     *
     * This method is deprecated. Functionality will be moved to a factory class.
     */
    abstract fun createInstance(instanceId: Int): IWorker

    override suspend fun launchInstances(count: Int) = coroutineScope {
        logger.info("🚀 Launching $count worker instances...")

        val jobs = List(count) { instanceId ->
            launch {
                val worker = createInstance(instanceId)
                logger.info("⚙️ Starting instance $instanceId of `${worker::class.simpleName}`...")

                try {
                    worker.start()
                    logger.info("✅ Instance $instanceId of `${worker::class.simpleName}` successfully started.")
                } catch (e: Exception) {
                    logger.error("❌ Instance $instanceId of `${worker::class.simpleName}` failed to start.", e)
                }
            }
        }
        jobs.joinAll()
        logger.info("🏁 All worker instances have completed execution.")
    }
}