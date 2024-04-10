expect fun tokenize(key: String): ByteArray
expect fun encryptBytes(token: ByteArray, data: ByteArray, iv: ByteArray): ByteArray
expect fun decryptBytes(token: ByteArray, data: ByteArray): ByteArray

class Enmoji {

    @OptIn(ExperimentalUnsignedTypes::class)
    companion object {
        // Define a mapping from each byte value to a unique emoji
        private val EMOJI_DICT = arrayOf(
            "😀", "😁", "😂", "😃", "😄", "😅", "😆", "😇", "😈", "👿", "😉", "😊", "😋", "😌", "😍", "😎",
            "😏", "😐", "😑", "😒", "😓", "😔", "😕", "😖", "😗", "😘", "😙", "😚", "😛", "😜", "😝", "😞",
            "😟", "😠", "😡", "😢", "😣", "😤", "😥", "😦", "😧", "😨", "😩", "😪", "😫", "😬", "😭", "😮",
            "😯", "😰", "😱", "😲", "😳", "😴", "😵", "😶", "😷", "😸", "😹", "😺", "😻", "😼", "😽", "😾",
            "😿", "🙀", "🙁", "🙂", "🙃", "🙄", "🙅", "🙆", "🙇", "🙈", "🙉", "🙊", "🙋", "🙌", "🙍", "🙎",
            "🙏", "🚀", "🚁", "🚂", "🚃", "🚄", "🚅", "🚆", "🚇", "🚈", "🚉", "🚊", "🚋", "🚌", "🚍", "🚎",
            "🚏", "🚐", "🚑", "🚒", "🚓", "🚔", "🚕", "🚖", "🚗", "🚘", "🚙", "🚚", "🚛", "🚜", "🚝", "🚞",
            "🚟", "🚠", "🚡", "🚢", "🚣", "🚤", "🚥", "🚦", "🚧", "🚨", "🚩", "🚪", "🚫", "🚬", "🚭", "🚮",
            "🚯", "🚰", "🚱", "🚲", "🚳", "🚴", "🚵", "🚶", "🚷", "🚸", "🚹", "🚺", "🚻", "🚼", "🚽", "🚾",
            "🚿", "🛀", "🛁", "🛂", "🛃", "🛄", "🛅", "🛋", "🛌", "🛍", "🛎", "🛏", "🛐", "🛑", "🛒", "🛠",
            "🛡", "🛢", "🛣", "🛤", "🛥", "🛩", "🛫", "🛬", "🛰", "🛳", "🛴", "🛵", "🛶", "🛷", "🛸", "🛹",
            "🛺", "🟠", "🟡", "🟢", "🟣", "🟤", "🟥", "🟧", "🟨", "🟩", "🟪", "🟫", "🤍", "🤎", "🤏", "🤐",
            "🤑", "🤒", "🤓", "🤔", "🤕", "🤖", "🤗", "🤘", "🤙", "🤚", "🤛", "🤜", "🤝", "🤞", "🤟", "🤠",
            "🤡", "🤢", "🤣", "🤤", "🤥", "🤦", "🤧", "🤨", "🤩", "🤪", "🤫", "🤬", "🤭", "🤮", "🤯", "🤰",
            "🤱", "🤲", "🤳", "🤴", "🤵", "🤶", "🤷", "🤸", "🤹", "🤺", "🤼", "🤽", "🤾", "🤿", "🥀", "🥁",
            "🥂", "🥃", "🥄", "🥅", "🥇", "🥈", "🥉", "🥊", "🥋", "🥌", "🥍", "🥎", "🥏", "🥐", "🥑", "🥥"
        )
        const val IV_LENGTH = 16

        // Reverse mapping from emoji to byte value
        private val EMOJI_TO_BYTE = EMOJI_DICT.let {
            val map = mutableMapOf<String, Byte>()
            it.forEachIndexed { index, s ->
                map[s] = index.toUByte().toByte()
            }
            map
        }

        private fun bytes2Enmoji(data: ByteArray): String {
            return data.toUByteArray().joinToString("") { EMOJI_DICT[it.toInt()] }
        }

        //😵🚏🤩🚡🚠🚑🚨🤩🚆🟤😘😊😡😩🥊🙌😆🚡🚤😵🚿🚱😶😟🤐🛎🛳🤨😼🛹🛹😂
        private fun enmoji2Bytes(enmojis: String): ByteArray {
            if (enmojis.isEmpty() || enmojis.length % 2 == 1) return ByteArray(0)

            val res = ByteArray(enmojis.length / 2)
            enmojis.chunked(2).forEachIndexed { i, emo ->
                if (!EMOJI_TO_BYTE.containsKey(emo)) return ByteArray(0)
                res[i] = EMOJI_TO_BYTE[emo]!!
            }
            return res
        }

        fun encrypt(digest: Digest, iv: ByteArray = ByteArray(IV_LENGTH)): String {
            val token = tokenize(digest.key)
            val data = digest.text.encodeToByteArray()
            val encrypted = encryptBytes(token, data, iv)
            return bytes2Enmoji(encrypted)/*.also{ println(it) }*/
        }

        fun decrypt(digest: Digest): String {
            val token = tokenize(digest.key)
            val encrypted = enmoji2Bytes(digest.enmoji)
            if (encrypted.isEmpty()) return ""
            val decrypted = decryptBytes(token, encrypted)
            return decrypted.decodeToString()/*.also{ println(it) }*/
        }
    }
}

internal fun ByteArray.isAllZero(): Boolean {
    return this.all { it == 0.toByte() }
}