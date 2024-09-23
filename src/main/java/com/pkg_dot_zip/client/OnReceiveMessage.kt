package com.pkg_dot_zip.client

import com.pkg_dot_zip.lib.ReceivedMessage

fun interface OnReceiveMessage {
    fun onReceiveMessage(message: ReceivedMessage)
}