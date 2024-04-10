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

actual fun encryptBytes(token: ByteArray, data: ByteArray, hiv: ByteArray): ByteArray {
    if (hiv.isAllZero()) SecureRandom().nextBytes(hiv)
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    val ivSpec = IvParameterSpec(hiv + hiv)
    cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(token, "AES"), ivSpec)
//    val paddedData = data.apply { padToBlockSize(cipher.blockSize) }
    val ciphertext = tryOrEmpty {
        cipher.doFinal(data)
    }
    // Combine IV and ciphertext and encode to Base64
    return hiv + ciphertext
}

actual fun decryptBytes(token: ByteArray, data: ByteArray): ByteArray {
    val hiv = data.copyOfRange(0, Enmoji.HIV_LENGTH)
    val ciphertext = data.copyOfRange(Enmoji.HIV_LENGTH, data.size)
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    val ivSpec = IvParameterSpec(hiv + hiv)
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
