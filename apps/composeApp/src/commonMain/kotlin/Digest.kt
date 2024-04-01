data class Digest(
    val text: String,
    val key: String,
    val enmoji: String
) {
    val isEncrypted get() = enmoji.isNotEmpty()
    val isDecrypted get() = !isEncrypted
    val displayText get() = enmoji.ifBlank { text }

    companion object {
        val Empty = Digest("", "", "")
    }
}