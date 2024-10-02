package com.pkg_dot_zip.lib

import com.pkg_dot_zip.lib.cia.Hasher
import java.net.DatagramPacket
import java.net.InetAddress

object PacketCreator {
    const val separationString = "\u2407"

    fun createMessagePacket(
        packetID: Int,
        usernameOfSender: String,
        content: String,
        address: InetAddress,
        port: Int
    ): DatagramPacket {
        return createPacket("$packetID;$usernameOfSender: $content", address, port)
    }

    fun createAcknowledgementPacket(packetID: Int, address: InetAddress, port: Int): DatagramPacket {
        return createPacket("ACK=$packetID", address, port)
    }

    fun createPacket(message: String, address: InetAddress, port: Int): DatagramPacket {
        val hash = Hasher().getHashedHexString(message)
        val fullMessage = hash + separationString + message

        return createPacket(fullMessage.toByteArray(), address, port)
    }

    private fun createPacket(data: ByteArray, address: InetAddress, port: Int): DatagramPacket {
        return DatagramPacket(data, data.size, address, port)
    }
}