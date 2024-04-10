import kotlin.test.Test
import kotlin.test.assertEquals

class EnmojiTest {

    @OptIn(ExperimentalStdlibApi::class)
    companion object {
        private const val HIV_HEX = "8080808080808080"
        private const val TOKEN_HEX = "ac859d43a1f6de0ecc2e32e3253cfc93b03acc46e7ebe0a7c26e5a9c3c1ab0b7"
        private const val ENCRYPTED_HEX = "808080808080808039eb0aed805e713ee345a421c255658e4d08c641b6b789ff231c8d0aacc9c42f"
        private val hiv = HIV_HEX.hexToByteArray()
        private val token = TOKEN_HEX.hexToByteArray()
        private val encrypted = ENCRYPTED_HEX.hexToByteArray()
        private val digest = Digest(
            text = "1234567890abcdefg",
            key = "thisisatoken",
            enmoji = "🚯🚯🚯🚯🚯🚯🚯🚯😸🤽😉🤿🚯🚍🚠😽🤴🙄🛥😠🤓🚄🚔🚽🙌😈🤗🙀🟥🟧🚸🥥😢😛🚼😉🛶🤚🤕😮"
        )

        private fun ByteArray.toHexString(): String {
            return this.joinToString("") { it.toHexString() }
        }
    }

    @Test
    fun tokenizeKey() {
        val actual = tokenize(digest.key).toHexString()
        val expected = TOKEN_HEX
        assertEquals(expected, actual)
    }

    @Test
    fun encryptTextToBytes() {
        val actual = encryptBytes(token, digest.text.encodeToByteArray(), hiv).toHexString()
        val expected = ENCRYPTED_HEX
        assertEquals(expected, actual)
    }

    @Test
    fun decryptBytes() {
        val actual = decryptBytes(token, encrypted).toHexString()
        val expected = digest.text.encodeToByteArray().toHexString()
        assertEquals(expected, actual)
    }

    @Test
    fun encryptTextToEnmoji() {
        val actual = Enmoji.encrypt(digest, hiv)
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