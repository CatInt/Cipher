import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.SecureRandom
import java.security.MessageDigest

actual fun tokenize(input: String): ByteArray {
    return input.toByteArray(Charsets.UTF_8).let {
        MessageDigest.getInstance("SHA-256").digest(it)
    }
}

actual fun encryptBytes(token: ByteArray, data: ByteArray): ByteArray {
    val iv = ByteArray(16)
    SecureRandom().nextBytes(iv)
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    val ivSpec = IvParameterSpec(iv)
    cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(token, "AES"), ivSpec)
//    val paddedData = data.apply { padToBlockSize(cipher.blockSize) }
    val ciphertext = tryOrEmpty {
        cipher.doFinal(data)
    }
    // Combine IV and ciphertext and encode to Base64
    return iv + ciphertext
}

actual fun decryptBytes(token: ByteArray, data: ByteArray): ByteArray {

    val iv = data.take(16).toByteArray() // The first 12 bytes are the nonce
    val ciphertext = data.drop(16).toByteArray()
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    val ivSpec = IvParameterSpec(iv)
    val secretKey = SecretKeySpec(token, "AES")
    cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
    val data = tryOrEmpty {
        cipher.doFinal(ciphertext)
    }
    return data
}

private inline fun tryOrEmpty(block: () -> ByteArray): ByteArray {
    return try {
        block()
    } catch (e: Exception) {
        ByteArray(0)
    }
}
