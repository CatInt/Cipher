import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.unit.dp
import cipher.composeapp.generated.resources.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun App() {
    MaterialTheme(
        colors = MaterialTheme.colors.copy(
            primary = AppTheme.Colour.Orange,
            background = AppTheme.Colour.Background
        )
    ) {
        var digest by remember { mutableStateOf(Digest.Empty) }
        var focusingOnText by remember { mutableStateOf(true) }
//        val color by remember(textDigest) { return if (it) AppTheme.Colour.Yellow else Color.Transparent}
        val focustRequester by remember { mutableStateOf(FocusRequester()) }
        val clipboardManager = LocalClipboardManager.current
        val scope = CoroutineScope(Dispatchers.IO)

        fun encrypt(d: Digest): String {
            if (d.isDecrypted) {
                Enmoji.encrypt(d).run {
                    digest = d.copy(enmoji = this)
                }
            }
            return digest.displayText
        }

        fun decrypt(d: Digest) {
            if (d.isEncrypted) {
                Enmoji.decrypt(d).run {
                    digest = d.copy(text = this, enmoji = "")
                }
            }
        }

        fun clearInput() {
            digest = digest.copy(text = "", enmoji = "")
            focusingOnText = true
        }

        if (focusingOnText) {
            log("focusOnText")
            LaunchedEffect(CoroutineScope(Dispatchers.Default)) {
                focusingOnText = false
                focustRequester.requestFocus()
            }
        }

        Column(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp).minimumInteractiveComponentSize()
        ) {
            // Text block
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
            ) {
                OutlinedTextField(
                    modifier = Modifier.matchParentSize().padding(top = 16.dp).focusRequester(focustRequester),
                    shape = AppTheme.Shape.Rounded,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = if (digest.isEncrypted) {
                            AppTheme.Colour.Yellowish
                        } else {
                            Color.Transparent
                        }
                    ),
                    enabled = digest.isDecrypted,
                    visualTransformation = {
                        TransformedText(it, OffsetMapping.Identity)
                    },
                    value = digest.displayText,
                    onValueChange = {
                        digest = digest.copy(text = it)
                        focustRequester.requestFocus()
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    label = {
                        val painter = painterResource(
                            if (digest.isDecrypted) Res.drawable.emj_chats else Res.drawable.emj_encrypted_chats
                        )
                        Image(painter, "Text", Modifier.size(40.dp))
                    },
                )
                Column(
                    modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (digest.isDecrypted) {
                        Button(
                            colors = ButtonDefaults.buttonColors(Color.Transparent),
                            elevation = null,
                            shape = AppTheme.Shape.Rounded,
                            onClick = { encrypt(digest) }
                        ) {
                            Image(
                                painterResource(Res.drawable.emj_unlock),
                                contentDescription = "Encrypte text"
                            )
                        }
                    } else {
                        Button(
                            colors = ButtonDefaults.buttonColors(Color.Transparent),
                            elevation = null,
                            shape = AppTheme.Shape.Rounded,
                            onClick = { decrypt(digest) }
                        ) {
                            Image(
                                painterResource(Res.drawable.emj_locked),
                                contentDescription = "Decrypt text"
                            )
                        }
                    }
                }
            }

            // Key block
            Box(modifier = Modifier.padding(top = 8.dp)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppTheme.Shape.Rounded,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = if (digest.isEncrypted) {
                            AppTheme.Colour.Gray
                        } else {
                            Color.Transparent
                        }
                    ),
                    enabled = digest.isDecrypted,
                    value = digest.key,
                    visualTransformation = keyVisualTransformation(),
                    singleLine = true,
                    onValueChange = { digest = digest.copy(key = it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            log("onDone")
                            focustRequester.requestFocus()
                        },
                        onGo = {

                            log("onGo")
                        }
                    ),
                    label = {
                        Image(painterResource(Res.drawable.emj_key), "Key", Modifier.size(24.dp))
                    },
                )
            }

            // Action block
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        colors = ButtonDefaults.buttonColors(AppTheme.Colour.Orange),
                        shape = AppTheme.Shape.Rounded,
                        onClick = {
                            scope.launch {
                                val enmoji = clipboardManager.getText()?.text ?: ""
                                decrypt(digest.copy(enmoji = enmoji, text = ""))
//                                focustRequester.freeFocus()
                            }
                        },
                    ) {
                        Image(
                            painterResource(Res.drawable.emj_paste),
                            contentDescription = "Clipboard"
                        )
                        Image(
                            painterResource(Res.drawable.emj_unlock),
                            contentDescription = "Decrypt"
                        )
                    }
                }
                Button(
                    colors = ButtonDefaults.buttonColors(Color.Transparent),
                    elevation = null,
                    shape = AppTheme.Shape.Rounded,
                    onClick = {
                        clearInput()
                    },
                ) {
                    Image(
                        painterResource(Res.drawable.emj_write),
                        contentDescription = "Compose"
                    )
                }
                Button(
                    colors = ButtonDefaults.buttonColors(AppTheme.Colour.Blue),
                    shape = RoundedCornerShape(6.dp),
                    onClick = {
                        encrypt(digest).also {
                            if (it.isNotBlank()) {
                                shareText(it)
                            }
                        }
                    }
                ) {
                    Image(
                        painterResource(Res.drawable.emj_locked),
                        contentDescription = "Encrypt"
                    )
                    Image(
                        painterResource(Res.drawable.emj_im),
                        contentDescription = "Send"
                    )
                }
            }
        }
    }
}
