package com.pkg_dot_zip.lib.cia

import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher


// FROM: https://stackoverflow.com/a/39615507
object Encryptor {
    private const val ALGORITHM: String = "RSA"

    fun encrypt(publicKey: ByteArray, inputData: ByteArray): ByteArray {
        val key = KeyFactory.getInstance(ALGORITHM)
            .generatePublic(X509EncodedKeySpec(publicKey))

        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key)

        return cipher.doFinal(inputData)
    }

    fun decrypt(privateKey: ByteArray, inputData: ByteArray): ByteArray {
        val key = KeyFactory.getInstance(ALGORITHM)
            .generatePrivate(PKCS8EncodedKeySpec(privateKey))

        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key)

        return cipher.doFinal(inputData)
    }

    fun generateKeyPair(): KeyPair {
        val random = SecureRandom.getInstance("SHA1PRNG", "SUN")

        val keyGen = KeyPairGenerator.getInstance(ALGORITHM)
        keyGen.initialize(512, random)

        return keyGen.generateKeyPair()
    }
}