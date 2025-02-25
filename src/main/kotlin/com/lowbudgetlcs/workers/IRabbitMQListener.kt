package com.lowbudgetlcs.workers

import com.rabbitmq.client.Delivery

/**
 * Defines properties and behavior for service workers listening on message queues.
 */
interface IMessageQListener {
    /**
     * Name of the queue emitted/listened on.
     */
    val queue: String

    /**
     * Defines the processing behavior of this service worker when recieving a [delivery].
     */
    fun processMessage(delivery: Delivery)
}