package com.lowbudgetlcs.bridges

import com.rabbitmq.client.*
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.addResourceSource
import io.ktor.util.logging.*
import java.net.ConnectException

data class RabbitMQConfig(val host: String)

class RabbitMQBridge(private val queue: String) {
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

    val channel: Channel = connect().createChannel().apply {
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
