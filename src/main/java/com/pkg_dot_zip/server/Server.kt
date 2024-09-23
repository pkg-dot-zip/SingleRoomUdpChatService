package com.pkg_dot_zip.server

import com.pkg_dot_zip.lib.Config
import com.pkg_dot_zip.lib.PacketCreator
import com.pkg_dot_zip.lib.ReceivedMessage
import com.pkg_dot_zip.lib.Status
import io.github.oshai.kotlinlogging.KotlinLogging
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

data class ClientInfo(val address: InetAddress, val port: Int, var status: Status)

private val logger = KotlinLogging.logger {}

class Server {
    private val clients = mutableMapOf<String, ClientInfo>()
    private val socket = DatagramSocket(Config.PORT)
    private val buffer = ByteArray(Config.BUFFER_SIZE)

    fun start() {
        logger.info { "Server started..." }
        while (true) {
            val packet = DatagramPacket(buffer, buffer.size)
            socket.receive(packet)
            val message = ReceivedMessage(packet)

            // Extract client's info (IP and port)
            val clientId = "${packet.address}:${packet.port}" // TODO: Retrieve username somehow.
            logger.info { "Received message from $clientId: $message" }

            if (!clients.containsKey(clientId)) {
                clients[clientId] = ClientInfo(packet.address, packet.port, Status.AVAILABLE)
            }

            if (message.isCommand()) {
                handleCommand(message, clientId)
            } else {
                broadcastMessage("$clientId: $message", clientId)
            }
        }
    }

    private fun handleCommand(command: ReceivedMessage, clientId: String) {
        return handleCommand(command.getString(), clientId)
    }

    private fun handleCommand(command: String, clientId: String) {
        when (command) {
            "/offline" -> {
                clients[clientId]?.status = Status.OFFLINE
                logger.info { "$clientId is now offline" }
                clients.remove(clientId)
                broadcastMessage("$clientId went offline", clientId)
            }

            "/available" -> {
                clients[clientId]?.status = Status.AVAILABLE
                logger.info { "$clientId is now available" }
                broadcastMessage("$clientId is available", clientId)
            }

            "/busy" -> {
                clients[clientId]?.status = Status.BUSY
                logger.info { "$clientId is now busy" }
                broadcastMessage("$clientId is busy", clientId)
            }

            else -> {
                logger.info { "Unknown command from $clientId: $command" }
            }
        }
    }

    private fun broadcastMessage(message: String, senderId: String) {
        for ((id, client) in clients) {
            if (id != senderId && client.status != Status.OFFLINE) {
                sendMessage(message, client.address, client.port)
            }
        }
    }

    private fun sendMessage(message: String, address: InetAddress, port: Int) {
        socket.send(PacketCreator.createPacket(message, address, port))
    }
}

fun main() {
    val server = Server()
    server.start()
}
