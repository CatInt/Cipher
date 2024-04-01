import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

actual fun shareText(content: String) {
    val stringSelection = StringSelection(content)
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    clipboard.setContents(stringSelection, null)
}

actual fun log(msg: String) {
    print("Cipher: $msg")
}