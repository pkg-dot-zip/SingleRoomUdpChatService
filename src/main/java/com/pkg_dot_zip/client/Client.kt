package com.pkg_dot_zip.client

import com.pkg_dot_zip.lib.Config
import com.pkg_dot_zip.lib.PacketCreator
import com.pkg_dot_zip.lib.ReceivedMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import java.net.DatagramPacket
import java.net.DatagramSocket
import kotlin.concurrent.thread

private val logger = KotlinLogging.logger {}

class Client {
    private val socket = DatagramSocket()
    private val buffer = ByteArray(Config.BUFFER_SIZE)

    @Volatile
    private var running = true

    fun start() {
        logger.info { "Client started. Type /available, /busy, /offline to change status." }

        // Thread to listen for incoming messages.
        thread {
            while (running) {
                try {
                    val packet = DatagramPacket(buffer, buffer.size)
                    socket.receive(packet)
                    val message = ReceivedMessage(packet)
                    logger.info { "Received: $message" }
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
            } else {
                sendMessage(input)
            }
        }

        socket.close()
    }

    private fun sendMessage(message: String) {
        val packet = PacketCreator.createPacket(message, Config.ADDRESS, Config.PORT)
        socket.send(packet)
        logger.info { "Sent: $message" }

        // TODO: What if message gets lost? :(
    }
}

fun main() {
    val client = Client()
    client.start()
}
