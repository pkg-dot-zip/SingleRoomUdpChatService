package com.pkg_dot_zip.client

import com.pkg_dot_zip.lib.events.Event
import com.pkg_dot_zip.lib.events.OnReceiveMessage

class ClientEvents {
    val onReceive = Event<OnReceiveMessage>()
}