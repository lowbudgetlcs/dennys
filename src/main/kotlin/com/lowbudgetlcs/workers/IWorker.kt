package com.lowbudgetlcs.workers

/**
 * Service worker contract.
 */
interface IWorker {
    /**
     * Execute the main task of this worker.
     *
     * This function should be implemented by concrete worker classes to define its main behavior.
     */
    fun start()

    /**
     * Launch [count] new instances of this worker on separate coroutines and call [start] on each of them.
     */
    suspend fun launchInstances(count: Int)
}