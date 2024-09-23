package com.pkg_dot_zip.client

import com.pkg_dot_zip.lib.Config
import com.pkg_dot_zip.lib.PacketCreator
import com.pkg_dot_zip.lib.ReceivedMessage
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

    @Volatile
    private var running = true

    private fun registerEvents() {
        logger.info { "Registering Events." }
        events.onReceive += {
            logger.info { "Received: $it" }
        }

        events.onReceive += OnReceiveMessage(::println) // Regular console output so that it looks normal to the user.
    }

    fun start() {
        registerEvents()
        logger.info { "Client started. Type /available, /busy, /offline to change status." }

        // Thread to listen for incoming messages.
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

        // Main thread to send messages
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

        socket.close()
    }

    private fun sendMessage(content: String, username: String = this.username) {
        val packet = PacketCreator.createMessagePacket(username, content, Config.ADDRESS, Config.PORT)
        socket.send(packet)
        logger.info { "Sent: $content" }

        // TODO: What if message gets lost? :(
    }
}

fun main() {
    val client = Client()
    client.start()
}
