package io.cipher

import App
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _appContext = application as Context
        setContent {
            App()
        }
    }

    companion object {
        private var _appContext: Context? = null
        val appContext get() = _appContext!!
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}