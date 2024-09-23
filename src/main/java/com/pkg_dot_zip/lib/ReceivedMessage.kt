package com.pkg_dot_zip.lib

import io.github.oshai.kotlinlogging.KotlinLogging
import java.net.DatagramPacket

private val logger = KotlinLogging.logger {}

class ReceivedMessage(private val packet: DatagramPacket) {
    fun isCommand(): Boolean = getContent().startsWith("/")

    fun getUsername(): String = getString().substringBefore(":").trim()
    fun getContent(): String = getString().substringAfter(":").trim()
    private fun getString(): String = String(packet.data, 0, packet.length).trim()

    override fun toString(): String = getString()
}