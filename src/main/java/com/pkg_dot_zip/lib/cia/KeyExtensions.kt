package com.pkg_dot_zip.lib.cia

import java.security.Key

object KeyExtensions {

    @OptIn(ExperimentalStdlibApi::class)
    fun Key.getString() : String = this.encoded.toHexString()
    @OptIn(ExperimentalStdlibApi::class)
    fun ByteArray.getString() : String = this.toHexString()
}