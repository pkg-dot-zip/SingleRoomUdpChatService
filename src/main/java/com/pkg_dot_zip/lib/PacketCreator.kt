package com.pkg_dot_zip.lib

import java.net.DatagramPacket
import java.net.InetAddress

object PacketCreator {
    fun createPacket(message: String, address: InetAddress, port: Int): DatagramPacket {
        return createPacket(message.toByteArray(), address, port)
    }

    fun createPacket(data: ByteArray, address: InetAddress, port: Int): DatagramPacket {
        return DatagramPacket(data, data.size, address, port)
    }
}