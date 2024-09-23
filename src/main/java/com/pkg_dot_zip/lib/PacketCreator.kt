package com.pkg_dot_zip.lib

import java.net.DatagramPacket
import java.net.InetAddress

object PacketCreator {
    fun createMessagePacket(
        usernameOfSender: String,
        content: String,
        address: InetAddress,
        port: Int
    ): DatagramPacket {
        return createPacket("$usernameOfSender: $content", address, port)
    }

    fun createPacket(message: String, address: InetAddress, port: Int): DatagramPacket {
        return createPacket(message.toByteArray(), address, port)
    }

    fun createPacket(data: ByteArray, address: InetAddress, port: Int): DatagramPacket {
        return DatagramPacket(data, data.size, address, port)
    }
}