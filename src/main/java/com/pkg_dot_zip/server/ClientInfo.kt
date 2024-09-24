package com.pkg_dot_zip.server

import java.net.InetAddress

data class ClientInfo(
    val address: InetAddress,
    val port: Int,
    var status: ClientStatus,
    var lastMessageTime: Long = System.currentTimeMillis()
)