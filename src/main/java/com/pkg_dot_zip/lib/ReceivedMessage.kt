package com.pkg_dot_zip.lib

import io.github.oshai.kotlinlogging.KotlinLogging
import java.net.DatagramPacket
import java.net.InetAddress

private val logger = KotlinLogging.logger {}

class ReceivedMessage(val packet: DatagramPacket) {

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
    fun getString(): String = getCompleteString().split(PacketCreator.separationString)[1]
    fun getHash(): String = getCompleteString().split(PacketCreator.separationString)[0]

    private fun getCompleteString() : String {
        return String(packet.data, 0, packet.length).trim()
    }

    override fun toString(): String = getString()
}