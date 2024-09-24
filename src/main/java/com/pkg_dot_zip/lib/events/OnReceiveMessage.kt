package com.pkg_dot_zip.lib.events

import com.pkg_dot_zip.lib.ReceivedMessage

fun interface OnReceiveMessage {
    fun onReceiveMessage(message: ReceivedMessage)
}