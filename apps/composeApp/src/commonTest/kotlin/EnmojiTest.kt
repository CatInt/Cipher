import kotlin.test.Test
import kotlin.test.assertEquals

class EnmojiTest {
    companion object {
        val digest = Digest(
            text = "1234567890abcdefg",
            key = "thisisatoken",
            enmoji = "😺🙅🥥😿🟫🥥🛫😀😊😦😓🥊😉🤦🤭🚎🟪😜🤬🛎😵🤭🚬🚲🛵🙉🚁🛏🚈🤽🚙🙋🥐🚳🛳🚆😎😶🚠🚤😡🤹😬😪😳🤗🛄🙂"
        )
    }

    @Test
    fun decrypt()
    {
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