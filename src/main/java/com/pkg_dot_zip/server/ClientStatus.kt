package com.pkg_dot_zip.server

typealias Status = ClientStatus

enum class ClientStatus {
    OFFLINE,
    AVAILABLE,
    BUSY;

    override fun toString(): String = this.name.lowercase()
}