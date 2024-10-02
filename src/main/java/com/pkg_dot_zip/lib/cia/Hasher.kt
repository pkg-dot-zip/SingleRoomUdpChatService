package com.pkg_dot_zip.lib.cia

import java.security.MessageDigest

class Hasher(private val algorithm: Algorithm = Algorithm.SHA_256) {
    private val md = MessageDigest.getInstance(algorithm.algorithm)

    fun getHashedBytes(bytes: ByteArray): ByteArray = md.digest(bytes)

    fun getHashedHexString(string: String): String = getHashedHexString(string.toByteArray())

    @OptIn(ExperimentalStdlibApi::class)
    fun getHashedHexString(bytes: ByteArray): String = getHashedBytes(bytes).toHexString()

    companion object {
        // List here: https://stackoverflow.com/questions/24979557/complete-list-of-messagedigest-available-in-the-jdk
        enum class Algorithm(val algorithm: String) {
            SHA("SHA"),
            SHA_224("SHA-224"),
            SHA_256("SHA-256"),
            SHA_384("SHA-384"),
            SHA_512("SHA-512"),
            MD2("MD2"),
            MD5("MD5"),
        }
    }
}