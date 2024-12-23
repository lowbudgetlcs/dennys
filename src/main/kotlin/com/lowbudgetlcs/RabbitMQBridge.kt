package com.lowbudgetlcs

import com.rabbitmq.client.*
import io.ktor.util.logging.*

class RabbitMQBridge(private val queue: String) {
    private val logger = KtorSimpleLogger("com.lowbudgetlcs.RabbitMQBridge")
    private val factory by lazy {
        ConnectionFactory().apply {
            host = "rabbitmq"
            isAutomaticRecoveryEnabled = true
            networkRecoveryInterval = 15000
        }
    }

    private val connection: Connection by lazy {
        factory.newConnection().also {
            logger.debug("Created new RabbitMQ connection.")
        }
    }

    val channel: Channel by lazy {
        connection.createChannel().apply {
            queueDeclare("CALLBACK", true, false, false, null)
            basicQos(1)
        }.also {
            logger.debug("Created new messageq channel.")
        }
    }

    fun emit(message: String) {
        channel.basicPublish("", queue, MessageProperties.PERSISTENT_TEXT_PLAIN, message.toByteArray(charset("UTF-8")))
            .also {
                logger.debug("Emitted {} on {}.", message, queue)
            }
    }

    fun listen(callback: DeliverCallback) {
        channel.run {
            basicConsume(queue, false, callback) { _ -> }
        }
    }
}
