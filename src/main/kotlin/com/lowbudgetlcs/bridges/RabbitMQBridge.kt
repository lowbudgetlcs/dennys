package com.lowbudgetlcs.bridges

import com.rabbitmq.client.*
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.addResourceSource
import io.ktor.util.logging.*

data class RabbitMQConfig(val host: String)

class RabbitMQBridge(private val queue: String) {
    @OptIn(ExperimentalHoplite::class)
    private val config =
        ConfigLoaderBuilder.default().withExplicitSealedTypes().addResourceSource("/rabbitmq.yaml").build()
            .loadConfigOrThrow<RabbitMQConfig>()
    private val logger = KtorSimpleLogger("com.lowbudgetlcs.RabbitMQBridge")
    private val factory by lazy {
        ConnectionFactory().apply {
            host = config.host
            isAutomaticRecoveryEnabled = true
        }
    }

    private val connection: Connection by lazy {
        factory.newConnection().also {
            logger.debug("Created new RabbitMQ connection.")
        }
    }

    val channel: Channel = connection.createChannel().apply {
        queueDeclare(queue, true, false, false, null)
        basicQos(1)
    }.also {
        logger.debug("Created new messageq channel.")
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
