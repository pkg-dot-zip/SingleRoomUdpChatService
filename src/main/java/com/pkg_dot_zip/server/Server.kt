package com.pkg_dot_zip.server

import com.pkg_dot_zip.lib.Config
import com.pkg_dot_zip.lib.PacketCreator
import com.pkg_dot_zip.lib.ReceivedMessage
import com.pkg_dot_zip.lib.cia.Hasher
import com.pkg_dot_zip.lib.cia.Keys
import io.github.oshai.kotlinlogging.KotlinLogging
import java.net.DatagramPacket
import java.net.DatagramSocket
import kotlin.time.Duration.Companion.minutes

private val logger = KotlinLogging.logger {}

class Server {
    private val clients = mutableMapOf<String, ClientInfo>()
    private val socket = DatagramSocket(Config.PORT)
    private val buffer = ByteArray(Config.BUFFER_SIZE)

    private val keys = Keys()
    private val events = ServerEvents()

    private fun registerEvents() {
        logger.info { "Registering Events." }

        // Log.
        events.onReceive += {
            logger.info { "Received message from ${it.clientId}: $it.message" }
        }

        // Add to list of clients connected.
        events.onReceive += {
            if (!clients.containsKey(it.clientId)) {
                clients[it.clientId] = ClientInfo(it.packet.address, it.packet.port, Status.AVAILABLE)
            }
        }

        // Send acknowledgement.
        events.onReceive += { message: ReceivedMessage ->
            socket.send(
                PacketCreator.createAcknowledgementPacket(
                    keys.public,
                    message.getID(),
                    message.getSenderAddress(),
                    message.getSenderPort()
                )
            )
        }

        events.onReceive += { checkIfUsersOffline() }

        // Fire proper event dependent on message type.
        events.onReceive += { msg: ReceivedMessage ->
            when {
                msg.isCommand() -> events.onReceiveCommand.invoke { it.onReceiveMessage(msg) }
                msg.isAcknowledgement() -> events.onReceiveAcknowledgement.invoke { it.onReceiveMessage(msg) }
                msg.isMessage() -> events.onReceiveTextMessage.invoke { it.onReceiveMessage(msg) }
            }
        }

        // Check hash.
        events.onReceive += { detectIfMessageModified(it) }

        events.onReceiveCommand += ::handleCommand
        events.onReceiveTextMessage += ::broadcastMessage
    }

    fun start() {
        registerEvents()
        logger.info { "Server started..." }
        while (true) {
            val packet = DatagramPacket(buffer, buffer.size)
            socket.receive(packet)
            events.onReceive.invoke { it.onReceiveMessage(ReceivedMessage(packet, keys.private)) }
        }
    }

    private fun handleCommand(command: ReceivedMessage) = handleCommand(command.getContent(), command.clientId)

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

    private fun broadcastMessage(msg: ReceivedMessage) =
        broadcastMessage("${msg.getUsername()}: ${msg.getContent()}", msg.clientId)

    private fun broadcastMessage(message: String, senderId: String) {
        for ((id, client) in clients) {
            if (id != senderId && client.status != Status.OFFLINE) {
                socket.send(PacketCreator.createPacket(keys.public, message, client.address, client.port))
            }
        }
    }

    private fun checkIfUsersOffline() {
        val currentTime = System.currentTimeMillis()
        val timeout = 5.minutes.inWholeMilliseconds

        for ((clientId, clientInfo) in clients) {
            if (clientInfo.status != Status.OFFLINE && currentTime - clientInfo.lastMessageTime > timeout) {
                logger.info { "$clientId has been set offline due to inactivity." }
                clientInfo.status = Status.OFFLINE
                broadcastMessage("$clientId went offline due to inactivity", clientId)
            }
        }
    }

    private fun detectIfMessageModified(msg: ReceivedMessage) {
        val originalHash = msg.getHash()
        val foundHash = Hasher().getHashedHexString(msg.getString())

        logger.info { "Original hash = $originalHash, foundHash = $foundHash" }

        if (foundHash == originalHash) return // Do nothing if correct.

        logger.warn { "Message was modified! 😢" }
    }
}

fun main() {
    val server = Server()
    server.start()
}
