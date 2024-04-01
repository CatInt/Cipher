import androidx.compose.runtime.Composable
import platform.Foundation.NSLog
import platform.UIKit.UIDevice
import platform.UIKit.*

class IOSPlatform : Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun shareText(content: String) {
    val activityViewController = UIActivityViewController(
        activityItems = listOf(content),
        applicationActivities = null
    )
    UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
        viewControllerToPresent = activityViewController,
        animated = true,
        completion = null
    )
}

// iOS-specific code
actual fun log(msg: String) {
    NSLog("Cipher", msg)
}