import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class EnmojiTest {
    companion object {
        private val iv = ByteArray(Enmoji.IV_LENGTH).also { it.fill(1) }
        private val token = byteArrayOf(-84, -123, -99, 67, -95, -10, -34, 14, -52, 46, 50, -29, 37, 60, -4, -109, -80, 58, -52, 70, -25, -21, -32, -89, -62, 110, 90, -100, 60, 26, -80, -73)
        private val encryptedBytes = iv + byteArrayOf(-4, -92, -58, -126, -100, 53, 29, -26, -127, 113, 2, -15, -73, -43, 66, 102, -29, 68, -23, -107, -87, 98, 44, 86, -77, 29, -120, -53, 36, 25, 94, 88)
        private val digest = Digest(
            text = "1234567890abcdefg",
            key = "thisisatoken",
            enmoji = "🚰🚰🚰🚰🚰🚰🚰🚰🚰🚰🚰🚰🚰🚰🚰🚰🚫😣🙅😂😛🟤🛑🚕😁🥃🚱🚠😶🚄🤓🤷🚒🤕🚘😔😨🤳🛶🤧😲🛑😈🙊🛥🛍🤯🤩"
        )
    }

    @Test
    fun tokenizeKey() {
        val actual = tokenize(digest.key)
        val expected = token
        assertContentEquals(expected, actual)
    }

    @Test
    fun encryptTextToBytes() {
        val actual = encryptBytes(token, digest.text.encodeToByteArray(), iv)
        val expected = encryptedBytes
        assertContentEquals(expected, actual)
    }

    @Test
    fun decryptBytes() {
        val actual = decryptBytes(token, encryptedBytes)
        val expected = digest.text.encodeToByteArray()
        assertContentEquals(expected, actual)
    }

    @Test
    fun encryptTextToEnmoji() {
        val actual = Enmoji.encrypt(digest, iv)
        val expected = digest.enmoji
        assertEquals(expected, actual)
    }

    @Test
    fun decryptEnmojiToText() {
        val actual = Enmoji.decrypt(digest)
        val expected = digest.text
        assertEquals(expected, actual)
    }
}