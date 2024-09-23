package com.pkg_dot_zip.server

import com.pkg_dot_zip.lib.Config
import com.pkg_dot_zip.lib.Status
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

data class ClientInfo(val address: InetAddress, val port: Int, var status: Status)

class Server {
    private val clients = mutableMapOf<String, ClientInfo>()
    private val socket = DatagramSocket(Config.PORT)
    private val buffer = ByteArray(Config.BUFFER_SIZE)

    fun start() {
        println("Server started...")
        while (true) {
            val packet = DatagramPacket(buffer, buffer.size)
            socket.receive(packet)
            val message = String(packet.data, 0, packet.length).trim()

            // Extract client's info (IP and port)
            val clientId = "${packet.address}:${packet.port}" // TODO: Retrieve username somehow.
            println("Received message from $clientId: $message")

            if (!clients.containsKey(clientId)) {
                clients[clientId] = ClientInfo(packet.address, packet.port, Status.AVAILABLE)
            }

            if (message.startsWith("/")) {
                handleCommand(message, clientId)
            } else {
                broadcastMessage("$clientId: $message", clientId)
            }
        }
    }

    private fun handleCommand(command: String, clientId: String) {
        when (command) {
            "/offline" -> {
                clients[clientId]?.status = Status.OFFLINE
                println("$clientId is now offline")
                clients.remove(clientId)
                broadcastMessage("$clientId went offline", clientId)
            }

            "/available" -> {
                clients[clientId]?.status = Status.AVAILABLE
                println("$clientId is now available")
                broadcastMessage("$clientId is available", clientId)
            }

            "/busy" -> {
                clients[clientId]?.status = Status.BUSY
                println("$clientId is now busy")
                broadcastMessage("$clientId is busy", clientId)
            }

            else -> {
                println("Unknown command from $clientId: $command")
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
        val data = message.toByteArray()
        val packet = DatagramPacket(data, data.size, address, port)
        socket.send(packet)
    }
}

fun main() {
    val server = Server()
    server.start()
}
