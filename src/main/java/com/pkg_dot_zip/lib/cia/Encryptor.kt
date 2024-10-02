package com.pkg_dot_zip.lib.cia

import com.pkg_dot_zip.lib.extension.KeyExtensions.getString
import io.github.oshai.kotlinlogging.KotlinLogging
import java.security.*
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

private val logger = KotlinLogging.logger {}

object Encryptor {
    data class MessageAndKey(val message: String, val encryptedAESKey: String)

    fun encrypt(message: String, recipientPublicKey: PublicKey): MessageAndKey {
        val aesKey = KeyGenerator.getInstance("AES").apply { init(128) }.generateKey()

        logger.info { "Encrypting message with key: ${aesKey.getString()}" }

        // Encrypt the message using AES.
        val aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey)
        val encryptedMessage = aesCipher.doFinal(message.toByteArray())

        // Encrypt the AES key using RSA (recipient's public key).
        logger.info { "Original AES Key: ${aesKey.getString()}" }
        val rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        rsaCipher.init(Cipher.ENCRYPT_MODE, recipientPublicKey)
        val encryptedAESKey = rsaCipher.doFinal(aesKey.encoded)
        logger.info { "Encrypted AES Key: ${Base64.getEncoder().encodeToString(encryptedAESKey)}" }

        return MessageAndKey(
            Base64.getEncoder().encodeToString(encryptedMessage),
            Base64.getEncoder().encodeToString(encryptedAESKey)
        )
    }

    fun decrypt(encryptedMessage: String, encryptedAESKey: String, privateKey: PrivateKey): String {
        logger.info { "Decrypting with privateKey ${privateKey.getString()}" }

        // Decrypt the AES key using RSA (private key).
        val rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedAESKeyBytes = rsaCipher.doFinal(Base64.getDecoder().decode(encryptedAESKey))
        logger.info { "Decrypted AES Key: ${Base64.getEncoder().encodeToString(decryptedAESKeyBytes)}" }
        
        // Reconstruct the AES key.
        val aesKey = SecretKeySpec(decryptedAESKeyBytes, "AES")

        logger.info { "Deconstructed AES key to ${aesKey.getString()}" }

        // Decrypt the message using AES.
        val aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey)
        val decryptedMessageBytes = aesCipher.doFinal(Base64.getDecoder().decode(encryptedMessage))

        return String(decryptedMessageBytes)
    }
}