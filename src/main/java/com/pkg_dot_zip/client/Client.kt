package com.pkg_dot_zip.client

import com.pkg_dot_zip.lib.Config
import com.pkg_dot_zip.lib.PacketCreator
import com.pkg_dot_zip.lib.ReceivedMessage
import com.pkg_dot_zip.lib.cia.KeyExtensions.getString
import com.pkg_dot_zip.lib.events.OnReceiveMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import java.net.DatagramPacket
import java.net.DatagramSocket
import kotlin.concurrent.thread

private val logger = KotlinLogging.logger {}

class Client {
    private val socket = DatagramSocket()
    private val buffer = ByteArray(Config.BUFFER_SIZE)
    private var username = "user${(100..999).random()}"
    private val events = ClientEvents()

    private var sentMessages = ArrayList<Int>(3)

    private var serverPublicKey: ByteArray = ByteArray(0)

    @Volatile
    private var running = true

    private fun registerEvents() {
        logger.info { "Registering Events." }

        events.onReceive += { logger.info { "Received: $it" } } // Log.

        // Fire proper event dependent on message type.
        events.onReceive += { msg: ReceivedMessage ->
            when {
                msg.isCommand() -> events.onReceiveCommand.invoke { it.onReceiveMessage(msg) }
                msg.isAcknowledgement() -> events.onReceiveAcknowledgement.invoke { it.onReceiveMessage(msg) }
                msg.isMessage() -> events.onReceiveTextMessage.invoke { it.onReceiveMessage(msg) }
                msg.isPublicKey() -> events.onReceivePublicKey.invoke { it.onReceiveMessage(msg) }
            }
        }

        events.onReceiveTextMessage += OnReceiveMessage(::println) // Regular console output so that it looks normal to the user.

        // Handle acknowledgement.
        events.onReceiveAcknowledgement += {
            // This means a packet was dropped; we sent 2 messages before any of them was acknowledged by the server.
            if (sentMessages.size >= 2) {
                logger.warn { "A packet was dropped containing message with id ${sentMessages.first()}" }
                sentMessages.subList(0, sentMessages.size - 1).clear() // Remove all but the last entry.
            }

            if (sentMessages.first() == it.getID()) {
                logger.info { "Acknowledged ${it.getID()}" }
                sentMessages.removeFirst()
            }
        }

        events.onReceivePublicKey += {
            // Only do once.
            if (serverPublicKey.isNotEmpty()) {
                logger.info { "Received public key ${it.getPublicKey().getString()}" }
                serverPublicKey = it.getPublicKey() }
            }
    }

    // Thread to listen for incoming messages.
    private fun receive() {
        thread {
            while (running) {
                try {
                    val packet = DatagramPacket(buffer, buffer.size)
                    socket.receive(packet)
                    events.onReceive.invoke { it.onReceiveMessage(ReceivedMessage(packet)) }
                } catch (e: Exception) {
                    if (running) logger.error(e) { "Error receiving message: ${e.message}" }
                }
            }
        }
    }

    // Main thread to send messages
    private fun send() {
        while (running) {
            val input = readlnOrNull()?.trim() ?: continue

            if (input == "/offline") {
                sendMessage(input)
                running = false
            } else if (input.startsWith("/username")) { // NOTE: We do this locally, since server identifies users by address.
                username = input.substringAfter("/username").trim()
            } else {
                sendMessage(input)
            }
        }
    }

    fun start() {
        registerEvents()
        logger.info { "Client started. Type /available, /busy, /offline to change status." }

        receive()
        send()

        socket.close()
    }

    private fun sendMessage(content: String, username: String = this.username) {
        val id = (0..Integer.MAX_VALUE).random()
        val packet = PacketCreator.createMessagePacket(id, username, content, Config.ADDRESS, Config.PORT, serverPublicKey)
        socket.send(packet)
        logger.info { "Sent: $content" }

        sentMessages.add(id)
    }
}

fun main() {
    val client = Client()
    client.start()
}
