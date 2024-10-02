package com.pkg_dot_zip.lib.cia

import java.security.KeyPairGenerator

class Keys() {
    private val keyPair = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.genKeyPair()
    val public = keyPair.public
    val private = keyPair.private
}