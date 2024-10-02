package com.pkg_dot_zip.lib

import com.pkg_dot_zip.lib.cia.Encryptor
import io.github.oshai.kotlinlogging.KotlinLogging
import java.net.DatagramPacket
import java.net.InetAddress
import java.security.PrivateKey

private val logger = KotlinLogging.logger {}

class ReceivedMessage(val packet: DatagramPacket, val privateKey: PrivateKey) {

    val clientId =
        "${packet.address}:${packet.port}" // NOTE: We identify users by address, not username, although that is what the user sees.

    fun getSenderAddress(): InetAddress = packet.address
    fun getSenderPort(): Int = packet.port
    fun isAcknowledgement(): Boolean = getString().startsWith("ACK=")
    fun isMessage(): Boolean = !isCommand() && !isAcknowledgement()
    fun isCommand(): Boolean = getContent().startsWith("/")

    fun getID(): Int {
        if (this.isMessage() || this.isCommand()) {
            return getString().substringBefore(";").trim().toInt()
        } else if (this.isAcknowledgement()) {
            return getString().substringAfter("ACK=").trim().toInt()
        }

        throw IllegalStateException("No ID found")
    }
    fun getUsername(): String = getString().substringBefore(":").substringAfter(";").trim()
    fun getContent(): String = getString().substringAfter(":").trim()

    fun getString(): String {
        val parts = getCompleteString().split(PacketCreator.SEPARATOR)
        val hash = parts[0]
        val aesKey = parts[1]
        val encryptedString = parts[2]

        logger.info { "Received Hash: $hash" }
        logger.info { "Received AES Key: $aesKey" }
        logger.info { "Received Encrypted Message: $encryptedString" }

        return Encryptor.decrypt(encryptedString, getAESKey(), privateKey)
    }
    fun getAESKey() : String = getCompleteString().split(PacketCreator.SEPARATOR)[1]
    fun getHash(): String = getCompleteString().split(PacketCreator.SEPARATOR)[0]

    private fun getCompleteString() : String {
        return String(packet.data, 0, packet.length).trim()
    }

    override fun toString(): String = getString()
}