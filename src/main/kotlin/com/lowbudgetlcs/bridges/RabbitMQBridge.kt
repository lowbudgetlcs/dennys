package com.lowbudgetlcs.bridges

import com.rabbitmq.client.*
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.addResourceSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.ConnectException

data class RabbitMQConfig(val host: String)

/**
 * Connects to RabbitMQ with automatic retry logic. Allows messages to be emitted to [queue],
 * or conversely listen on [queue].
 */
class RabbitMQBridge(private val queue: String) {
    /**
     * One-time RabbitMQ configuration.
     */
    companion object {
        @OptIn(ExperimentalHoplite::class)
        private val config =
            ConfigLoaderBuilder.default().withExplicitSealedTypes().addResourceSource("/rabbitmq.yaml").build()
                .loadConfigOrThrow<RabbitMQConfig>()
        private val logger : Logger = LoggerFactory.getLogger(RabbitMQBridge::class.java)
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
                    logger.debug("🐇🔌 Successfully connected to RabbitMQ at `${config.host}`.")
                }
            } catch (e: ConnectException) {
                attempt++
                logger.error("❌ Failed to connect to RabbitMQ (attempt $attempt/5), retrying in 5 seconds...", e)
                Thread.sleep(5000)
            }
        }
        throw IllegalStateException("🚨 RabbitMQ connection failed after 5 attempts.")
    }

    /**
     * Lightweight connection object allowing read and writes to [queue].
     */
    val channel: Channel = connect().createChannel().apply {
        queueDeclare(queue, true, false, false, null)
        basicQos(1)
    }.also {
        logger.info("📨 Channel established for queue `$queue`.")
    }

    /**
     * Write [message] onto [queue].
     */
    fun emit(message: String) {
        channel.basicPublish("", queue, MessageProperties.PERSISTENT_TEXT_PLAIN, message.toByteArray(charset("UTF-8")))
            .also {
                logger.info("📤 Emitted message to queue `$queue`: $message")
            }
    }

    /**
     * Listen on [queue] and call [callback] on all messages recieved.
     */
    fun listen(callback: DeliverCallback) {
        logger.info("👂 Listening for messages on queue `$queue`...")
        channel.run {
            basicConsume(queue, false, callback) { _ -> }
        }
    }
}
