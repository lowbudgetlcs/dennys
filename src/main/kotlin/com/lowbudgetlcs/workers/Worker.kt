package com.lowbudgetlcs.workers

interface Worker {
    fun start()
    suspend fun launchInstances(count: Int)
}