package com.pkg_dot_zip.lib

import java.net.DatagramPacket
import java.net.InetAddress

object PacketCreator {
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
        return createPacket(message.toByteArray(), address, port)
    }

    fun createPacket(data: ByteArray, address: InetAddress, port: Int): DatagramPacket {
        return DatagramPacket(data, data.size, address, port)
    }
}