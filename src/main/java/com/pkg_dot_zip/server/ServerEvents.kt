package com.pkg_dot_zip.server

import com.pkg_dot_zip.lib.events.Event
import com.pkg_dot_zip.lib.events.OnReceiveMessage

class ServerEvents {
    val onReceive = Event<OnReceiveMessage>()
}