package com.lowbudgetlcs.workers

import com.rabbitmq.client.Delivery

interface RabbitMQWorker {
    val queue: String
    fun processMessage(delivery: Delivery)
}