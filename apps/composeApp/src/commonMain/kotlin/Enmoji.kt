expect fun tokenize(key: String): ByteArray
expect fun encryptBytes(token: ByteArray, data: ByteArray, iv: ByteArray): ByteArray
expect fun decryptBytes(token: ByteArray, data: ByteArray): ByteArray

class Enmoji {
    
    companion object {
        // Define a mapping from each byte value to a unique emoji
        private val EMOJI_MAP = arrayOf(
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
        private val EMOJI_TO_BYTE = EMOJI_MAP.let {
            val map = mutableMapOf<String, Byte>()
            it.forEachIndexed { index, s ->
                map[s] = (index - 128).toByte()
//                print("${s}")
            }
            map
        }

        private fun bytes2Enmoji(data: ByteArray): String {
//            print(data.joinToString(","))
            return data.joinToString("") { EMOJI_MAP[it.toInt() + 128] }
        }

        //😵🚏🤩🚡🚠🚑🚨🤩🚆🟤😘😊😡😩🥊🙌😆🚡🚤😵🚿🚱😶😟🤐🛎🛳🤨😼🛹🛹😂
        private fun enmoji2Bytes(enmoji: String): ByteArray {
//            println("enmoji2Bytes ${enmoji.length} ${EMOJI_TO_BYTE.size}")
            if (enmoji.isEmpty() || enmoji.length % 2 == 1) return ByteArray(0)

            val res = ByteArray(enmoji.length / 2)
            for (i in 0..res.lastIndex) {
                val emoji = "${enmoji[i * 2]}${enmoji[i * 2 + 1]}".also {
                    if (!EMOJI_TO_BYTE.containsKey(it)) return ByteArray(0)
                }
                res[i] = EMOJI_TO_BYTE[emoji]!!
            }
            return res
        }

        fun encrypt(digest: Digest, iv: ByteArray = ByteArray(Enmoji.IV_LENGTH)): String {
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