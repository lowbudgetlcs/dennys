package com.lowbudgetlcs

import com.rabbitmq.client.DeliverCallback
import com.rabbitmq.client.Delivery
import io.ktor.util.logging.*

class TournamentEngine {
    private val queue = "CALLBACK"
    private val logger = KtorSimpleLogger("com.lowbudgetlcs.TournamentEngine")
    // Configure Database
    private val lblcs = DatabaseBridge().db
    fun main() {
        logger.info("TournamentEngine running...")
        logger.info("Divisions: ${lblcs.divisionsQueries.selectAll().executeAsList()}")
        val bridge = RabbitMQBridge("CALLBACK")
        listen(bridge)
    }

    private fun listen(bridge: RabbitMQBridge) {
       logger.debug("Listening on $queue...")
        val readRiotCallback = DeliverCallback { _, delivery: Delivery ->
            logger.debug("[x] Received message on '${delivery.envelope.deliveryTag}.")
            val message = String(delivery.body, charset("UTF-8"))
            logger.debug("[x] Message: {}", message)
            bridge.channel.basicAck(delivery.envelope.deliveryTag, false)
        }
        bridge.listen(readRiotCallback)
    }
}