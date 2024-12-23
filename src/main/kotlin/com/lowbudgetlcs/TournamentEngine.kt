package com.lowbudgetlcs

import com.rabbitmq.client.Delivery
import com.rabbitmq.client.DeliverCallback
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource
import io.ktor.util.logging.*
import kotlinx.serialization.json.Json

class TournamentEngine {
    private val queue = "CALLBACK"
    private val logger = KtorSimpleLogger("com.lowbudgetlcs.TournamentEngine")
    // Configure Database
    private val config =
        ConfigLoaderBuilder.default().addResourceSource("/database.yaml").build().loadConfigOrThrow<Config>()
    private val lblcs = Lblcs(config.lblcs).db
    private val local = Local(config.local).db
    fun main() {
        logger.info("TournamentEngine running...")
        logger.info("Divisions: ${lblcs.divisionsQueries.selectAll().executeAsList()}")
        logger.info("Results: ${local.resultsQueries.selectAll().executeAsList()}")
        val bridge = RabbitMQBridge("CALLBACK")
        listen(bridge)
    }

    private fun listen(bridge: RabbitMQBridge) {
       logger.info("Listening on $queue...")
        val readRiotCallback = DeliverCallback { _, delivery: Delivery ->
            logger.info("[x] Received message on '${delivery.envelope.deliveryTag}.")
            val message = String(delivery.body, charset("UTF-8"))
            logger.debug("[x] Message: {}", message)
            bridge.channel.basicAck(delivery.envelope.deliveryTag, false)
        }
        bridge.listen(readRiotCallback)
    }
}