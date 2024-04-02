import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.MessageDigest
import java.security.SecureRandom

actual fun tokenize(key: String): ByteArray {
    return key.encodeToByteArray().let {
        MessageDigest.getInstance("SHA-256").digest(it)
    }
}

actual fun encryptBytes(token: ByteArray, data: ByteArray, iv: ByteArray): ByteArray {
    if (iv.isAllZero()) SecureRandom().nextBytes(iv)
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
    val iv = data.copyOfRange(0, Enmoji.IV_LENGTH)
    val ciphertext = data.copyOfRange(Enmoji.IV_LENGTH, data.size)
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    val ivSpec = IvParameterSpec(iv)
    val secretKey = SecretKeySpec(token, "AES")
    cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
    return tryOrEmpty {
        cipher.doFinal(ciphertext)
    }
}

private inline fun tryOrEmpty(block: () -> ByteArray): ByteArray {
    return try {
        block()
    } catch (e: Exception) {
        ByteArray(0)
    }
}
