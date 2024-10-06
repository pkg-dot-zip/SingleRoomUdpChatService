package com.pkg_dot_zip.lib

import com.pkg_dot_zip.lib.cia.Encryptor
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
        port: Int,
        publicKey: ByteArray? = null
    ): DatagramPacket {
        return createPacket("$packetID;$usernameOfSender: $content", address, port, publicKey)
    }

    fun createAcknowledgementPacket(packetID: Int, address: InetAddress, port: Int, publicKey: ByteArray? = null): DatagramPacket {
        return createPacket("ACK=$packetID", address, port, publicKey)
    }

    fun createPacket(message: String, address: InetAddress, port: Int, publicKey: ByteArray? = null): DatagramPacket {
        val hash = Hasher().getHashedHexString(message)
        val fullMessage = hash + separationString + message

        return createPacket(fullMessage.toByteArray(), address, port, publicKey)
    }

    fun createKeyPacket(data: ByteArray, address: InetAddress, port: Int, publicKey: ByteArray? = null): DatagramPacket {
        return createPacket(data, address, port, publicKey) // NOTE: No hash.
    }

    private fun createPacket(data: ByteArray, address: InetAddress, port: Int, publicKey: ByteArray? = null): DatagramPacket {
        var dataToSend = data
        if (publicKey != null && publicKey.size > 1) {
            dataToSend = Encryptor.encrypt(publicKey, data)
        }
        return DatagramPacket(dataToSend, dataToSend.size, address, port)
    }
}