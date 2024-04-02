import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class EnmojiTest {
    companion object {
        val iv = ByteArray(Enmoji.IV_LENGTH).also { it.fill(1) }
        val token = byteArrayOf(-84,-123,-99,67,-95,-10,-34,14,-52,46,50,-29,37,60,-4,-109,-80,58,-52,70,-25,-21,-32,-89,-62,110,90,-100,60,26,-80,-73)
        val encrypted = byteArrayOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, -4, -92, -58, -126, -100, 53, 29, -26, -127, 113, 2, -15, -73, -43, 66, 102, -29, 68, -23, -107, -87, 98, 44, 86, -77, 29, -120, -53, 36, 25, 94, 88)
        val digest = Digest(
            text = "1234567890abcdefg",
            key = "thisisatoken",
            enmoji = "😺🙅🥥😿🟫🥥🛫😀😊😦😓🥊😉🤦🤭🚎🟪😜🤬🛎😵🤭🚬🚲🛵🙉🚁🛏🚈🤽🚙🙋🥐🚳🛳🚆😎😶🚠🚤😡🤹😬😪😳🤗🛄🙂"
        )
    }

    @Test
    fun hash() {
        val res = tokenize(digest.key)
        assertContentEquals(token, res)
    }


    @Test
    fun encrypt() {
        val res = encryptBytes(token, digest.text.encodeToByteArray(), iv)
        assertContentEquals(encrypted, res)
    }

    @Test
    fun decrypt() {
        val res = Enmoji.decrypt(digest)
        assertEquals(digest.text, res)
    }
    
    @Test
    fun encryptAndDecrypt() {
        val enmoji = Enmoji.encrypt(digest)
        val res = Enmoji.decrypt(digest.copy(enmoji = enmoji))
        assertEquals(digest.text, res)
    }
}