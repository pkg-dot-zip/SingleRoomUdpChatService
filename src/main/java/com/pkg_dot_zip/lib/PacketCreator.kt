package com.pkg_dot_zip.lib

import com.pkg_dot_zip.lib.cia.Encryptor
import com.pkg_dot_zip.lib.cia.Hasher
import com.pkg_dot_zip.lib.extension.KeyExtensions.getString
import io.github.oshai.kotlinlogging.KotlinLogging
import java.net.DatagramPacket
import java.net.InetAddress
import java.security.PublicKey

private val logger = KotlinLogging.logger {}

object PacketCreator {
    const val SEPARATOR = "\u2407"

    fun createMessagePacket(
        publicKey: PublicKey,
        packetID: Int,
        usernameOfSender: String,
        content: String,
        address: InetAddress,
        port: Int,
    ): DatagramPacket {
        return createPacket(publicKey, "$packetID;$usernameOfSender: $content", address, port)
    }

    fun createAcknowledgementPacket(
        publicKey: PublicKey,
        packetID: Int,
        address: InetAddress,
        port: Int
    ): DatagramPacket {
        return createPacket(publicKey, "ACK=$packetID", address, port)
    }

    fun createPacket(publicKey: PublicKey, message: String, address: InetAddress, port: Int): DatagramPacket {
        logger.info { "Creating packet with publicKey: ${publicKey.getString()}" }

        val hash = Hasher().getHashedHexString(message)
        val (encryptedMessage, key) = Encryptor.encrypt(message, publicKey)
        val fullMessage = hash + SEPARATOR + key + SEPARATOR + encryptedMessage

        // Log the components before sending
        logger.info { "Hash: $hash" }
        logger.info { "AES Key: $key" }
        logger.info { "Encrypted Message: $encryptedMessage" }

        return createPacket(fullMessage.toByteArray(), address, port)
    }

    private fun createPacket(data: ByteArray, address: InetAddress, port: Int): DatagramPacket {
        return DatagramPacket(data, data.size, address, port)
    }
}