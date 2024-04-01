import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

fun keyVisualTransformation() = object : VisualTransformation {
    val mask: Char = '\u2022'
    override fun filter(text: AnnotatedString): TransformedText {
        if (text.text.isEmpty()) return TransformedText(AnnotatedString(""), OffsetMapping.Identity)
        return TransformedText(
            AnnotatedString(mask.toString().repeat(text.text.length)),
//            AnnotatedString(mask.toString().repeat(text.text.length - 1) + text.text.last()),
            OffsetMapping.Identity
        )
    }

//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (other !is PasswordVisualTransformation) return false
//        if (mask != other.mask) return false
//        return true
//    }
//
//    override fun hashCode(): Int {
//        return mask.hashCode()
//    }
}