package com.lowbudgetlcs.workers

interface Worker {
    val queue: String
    fun start()
}