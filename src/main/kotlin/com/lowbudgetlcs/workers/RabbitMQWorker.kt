package com.lowbudgetlcs.workers

import com.rabbitmq.client.Delivery

interface RabbitMQWorker {
    val queue: String
    fun start()
    fun processMessage(delivery: Delivery)
}