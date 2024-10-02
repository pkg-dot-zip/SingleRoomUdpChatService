package com.pkg_dot_zip.lib.extension

import java.security.Key

object KeyExtensions {

    @OptIn(ExperimentalStdlibApi::class)
    fun Key.getString(): String = this.encoded.toHexString()
}