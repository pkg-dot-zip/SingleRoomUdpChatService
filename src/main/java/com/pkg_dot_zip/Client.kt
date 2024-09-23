package com.pkg_dot_zip

import java.net.DatagramPacket
import java.net.DatagramSocket
import kotlin.concurrent.thread

class Client {
    private val socket = DatagramSocket()
    private val buffer = ByteArray(Server.BUFFER_SIZE)

    @Volatile
    private var running = true

    fun start() {
        println("Client started. Type /available, /busy, /offline to change status.")

        // Thread to listen for incoming messages.
        thread {
            while (running) {
                try {
                    val packet = DatagramPacket(buffer, buffer.size)
                    socket.receive(packet)
                    val message = String(packet.data, 0, packet.length).trim()
                    println("Received: $message")
                } catch (e: Exception) {
                    if (running) println("Error receiving message: ${e.message}")
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
        val data = message.toByteArray()
        val packet = DatagramPacket(data, data.size, Server.ADDRESS, Server.PORT)
        socket.send(packet)
        println("Sent: $message")

        // TODO: What if message gets lost? :(
    }
}

fun main() {
    val client = Client()
    client.start()
}
