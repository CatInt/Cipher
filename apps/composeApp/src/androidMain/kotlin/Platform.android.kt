import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.createChooser
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import io.cipher.MainActivity

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun shareText(content: String) {
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra (Intent.EXTRA_TEXT, content)
        type = "text/plain"
    }
    val context = MainActivity.appContext
    startActivity(context, createChooser(shareIntent, "分享").addFlags(FLAG_ACTIVITY_NEW_TASK), null)
}

actual fun log(msg: String) {
    Log.d("Cipher", msg)
}