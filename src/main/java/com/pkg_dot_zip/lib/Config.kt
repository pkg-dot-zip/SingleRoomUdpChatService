package com.pkg_dot_zip.lib

import java.net.InetAddress

object Config {
    const val PORT = 9876
    const val BUFFER_SIZE = 1024
    val ADDRESS = InetAddress.getByName("localhost")
}