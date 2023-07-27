package com.turo.infra.messaging

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import org.springframework.beans.factory.annotation.Value

import org.springframework.stereotype.Component

@Component
class EventDispatcher(
    @Value("\${rabbitmq.host}") private val rabbitmqHost: String,
    @Value("\${rabbitmq.port}") private val rabbitmqPort: Int
) {
    private lateinit var connection: Connection
    private lateinit var channel: Channel

    init {
        initialize()
    }

    private fun initialize() {
        val factory = ConnectionFactory()
        factory.host = rabbitmqHost
        factory.port = rabbitmqPort

        connection = factory.newConnection()
        channel = connection.createChannel()
    }

    fun dispatchEvent(exchange: String, routingKey: String, event: String) {
        channel.basicPublish(exchange, routingKey, null, event.toByteArray())
        println("Event dispatched successfully. Exchange: $exchange, Routing Key: $routingKey")
    }
}
