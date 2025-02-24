package com.lowbudgetlcs.bridges

import com.rabbitmq.client.*
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.addResourceSource
import io.ktor.util.logging.*
import java.net.ConnectException

data class RabbitMQConfig(val host: String)

/**
 * Connects to RabbitMQ with automatic retry logic. Allows messages to be emitted to [queue],
 * or conversely listen on [queue].
 */
class RabbitMQBridge(private val queue: String) {
    /**
     * One-time RabbitMQ configuration. Runs once on startup.
     */
    companion object {
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
    }

    /**
     * Creates a RabbitMQ [Connection]. This is a long-lived connection that should live for the
     * entire lifecyle of the object.
     */
    private fun connect(): Connection {
        var attempt = 0
        while (attempt < 5) {
            try {
                return factory.newConnection().also {
                    logger.debug("Created new RabbitMQ connection.")
                }
            } catch (e: ConnectException) {
                attempt++
                logger.error("Failed to connect to RabbitMQ (attempt $attempt/5), retrying in 5 seconds...")
                logger.error(e.message)
                Thread.sleep(5000)
            }
        }
        throw IllegalStateException("This should never be reached") // Just in case
    }

    /**
     * Lightweight connection object allowing read and writes to [queue].
     */
    val channel: Channel = connect().createChannel().apply {
        queueDeclare(queue, true, false, false, null)
        basicQos(1)
    }.also {
        logger.debug("Created new messageq channel.")
    }

    /**
     * Write [message] onto [queue].
     */
    fun emit(message: String) {
        channel.basicPublish("", queue, MessageProperties.PERSISTENT_TEXT_PLAIN, message.toByteArray(charset("UTF-8")))
            .also {
                logger.debug("Emitted {} on {}.", message, queue)
            }
    }

    /**
     * Listen on [queue] and call [callback] on all messages recieved.
     */
    fun listen(callback: DeliverCallback) {
        channel.run {
            basicConsume(queue, false, callback) { _ -> }
        }
    }
}
