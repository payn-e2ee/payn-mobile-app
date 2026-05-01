package com.example.payn.core.data.network

import android.util.Log
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish
import io.ktor.utils.io.core.toByteArray

class MqttWebSocketClient {
    private var mqttClient: Mqtt3AsyncClient? = null
    private var isConnected = false

    fun connect(
        identifier: String,
        username: String,
        password: String,
        serverHost: String,
        serverPort: Int,
        serverPath: String,
        onConnected: () -> Unit
    ) {
        mqttClient = MqttClient.builder()
            .useMqttVersion3()
            .identifier(identifier)
            .serverHost(serverHost)
            .serverPort(serverPort)
            .simpleAuth()
            .username(username)
            .password(password.toByteArray())
            .applySimpleAuth()
            .webSocketConfig()
            .serverPath(serverPath)
            .subprotocol("mqtt")
            .applyWebSocketConfig()
            .buildAsync()

        mqttClient!!.connectWith()
            .send()
            .whenComplete { _, throwable ->
                if (throwable != null) {
                    Log.i("MqttWebSocketClient", "Error connecting: ${throwable.message}")
                    return@whenComplete
                }
                isConnected = true
                Log.i("MqttWebSocketClient", "Connected")
                onConnected()
            }
    }

    fun disconnect() {
        requireConnected().disconnect()
    }

    private fun requireConnected(): Mqtt3AsyncClient {
        val client = mqttClient
        check(client != null && isConnected) {
            "MQTT client is not connected. Call connect() first."
        }
        return client
    }

    fun publish(topic: String, payload: ByteArray) {
        val client = requireConnected()

        client.publishWith()
            .topic(topic)
            .payload(payload)
            .send()
            .whenComplete { _, throwable ->
                if (throwable != null) {
                    Log.i("MqttWebSocketClient", "Error publishing: ${throwable.message}")
                    return@whenComplete
                }
                Log.i("MqttWebSocketClient", "published successfully")
            }
    }

    fun subscribe(topic: String, callback: (Mqtt3Publish) -> Unit) {
        val client = requireConnected()

        client.subscribeWith()
            .topicFilter(topic)
            .callback(callback)
            .send()
            .whenComplete { _, throwable ->
                if (throwable != null) {
                    Log.i("MqttWebSocketClient", "Error subscribing: ${throwable.message}")
                    return@whenComplete
                }
                Log.i("MqttWebSocketClient", "subscribed successfully")
            }
    }
}