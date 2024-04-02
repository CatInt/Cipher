import kotlinx.cinterop.*
import platform.CoreCrypto.*
import platform.posix.size_tVar

@OptIn(ExperimentalForeignApi::class)
actual fun tokenize(key: String): ByteArray {
    return memScoped {
        ByteArray(256).run {
            val token = allocArray<UByteVar>(CC_SHA256_DIGEST_LENGTH)
            val keyInBytes = key.encodeToByteArray()
            CC_SHA256(keyInBytes.refTo(0), key.length.convert(), token)
            token.readBytes(CC_SHA256_DIGEST_LENGTH)
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
actual fun encryptBytes(token: ByteArray, data: ByteArray, iv: ByteArray): ByteArray {
    return memScoped {
        if (iv.isAllZero()) {
            CCRandomGenerateBytes(iv.refTo(0), iv.size.convert())
        }
        cryptorScope { cryptorRef ->
            cryptorRef.create(kCCEncrypt, token, iv.refTo(0))

            val output = ByteArray(cryptorRef.outputLength(data.size))
            val dataOutMoved = alloc<size_tVar>()

            val moved = cryptorRef.update(data, output, dataOutMoved)
            if (output.size != moved) {
                cryptorRef.final(
                    dataOut = output.refTo(moved),
                    dataOutAvailable = output.size - moved,
                    dataOutMoved = dataOutMoved,
                )
            }

            iv + output
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
actual fun decryptBytes(token: ByteArray, data: ByteArray): ByteArray {
    val iv = data.copyOfRange(0, Enmoji.IV_LENGTH)
    val ciphertext = data.copyOfRange(Enmoji.IV_LENGTH, data.size)
    return memScoped {
        cryptorScope { cryptorRef ->
            cryptorRef.create(kCCDecrypt, token, iv.refTo(0))

            val output = ByteArray(cryptorRef.outputLength(ciphertext.size))
            val dataOutMoved = alloc<size_tVar>()

            var moved = cryptorRef.update(ciphertext, output, dataOutMoved)
            if (output.size != moved) {
                moved += cryptorRef.final(
                    dataOut = output.refTo(moved),
                    dataOutAvailable = output.size - moved,
                    dataOutMoved = dataOutMoved
                )
            }

            if (output.size == moved) {
                output
            } else {
                output.copyOf(moved)
            }
        }
    }
}


@OptIn(ExperimentalForeignApi::class)
private fun CCCryptorRefVar.outputLength(inputLength: Int): Int {
    return CCCryptorGetOutputLength(
        cryptorRef = value,
        inputLength = inputLength.convert(),
        final = true
    ).convert()
}

@OptIn(ExperimentalForeignApi::class)
private inline fun <T> MemScope.cryptorScope(block: (cryptorRef: CCCryptorRefVar) -> T): T {
    val cryptorRef = alloc<CCCryptorRefVar>()
    try {
        return block(cryptorRef)
    } finally {
        CCCryptorRelease(cryptorRef.value)
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun CCCryptorRefVar.create(op: CCOperation, token: ByteArray, iv: CValuesRef<*>) {
    checkResult(
        CCCryptorCreateWithMode(
            op = op,
            cryptorRef = ptr,
            alg = kCCAlgorithmAES,
            mode = kCCModeCBC,
            padding = kCCOptionPKCS7Padding,
            key = token.refTo(0),
            keyLength = token.size.convert(),
            iv = iv,

            // unused options
            options = 0.convert(),
            tweak = null,
            tweakLength = 0.convert(),
            numRounds = 0,
        )
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun CCCryptorRefVar.update(
    dataIn: ByteArray,
    output: ByteArray,
    dataOutMoved: size_tVar,
): Int {
    checkResult(
        CCCryptorUpdate(
            cryptorRef = value,
            dataIn = dataIn.refTo(0),
            dataInLength = dataIn.size.convert(),
            dataOut = output.refTo(0),
            dataOutAvailable = output.size.convert(),
            dataOutMoved = dataOutMoved.ptr
        )
    )
    return dataOutMoved.value.convert()
}

@OptIn(ExperimentalForeignApi::class)
private fun CCCryptorRefVar.final(
    dataOut: CValuesRef<*>,
    dataOutAvailable: Int,
    dataOutMoved: size_tVar,
): Int {
    checkResult(
        CCCryptorFinal(
            cryptorRef = value,
            dataOut = dataOut,
            dataOutAvailable = dataOutAvailable.convert(),
            dataOutMoved = dataOutMoved.ptr
        )
    )
    return dataOutMoved.value.convert()
}

private fun checkResult(result: CCCryptorStatus) {
    val error = when (result) {
        kCCSuccess -> return
        kCCParamError -> "Illegal parameter value."
        kCCBufferTooSmall -> "Insufficent buffer provided for specified operation."
        kCCMemoryFailure -> "Memory allocation failure."
        kCCAlignmentError -> "Input size was not aligned properly."
        kCCDecodeError -> "Input data did not decode or decrypt properly."
        kCCUnimplemented -> "Function not implemented for the current algorithm."
        else -> "CCCrypt failed with code $result"
    }
    throw Error(error)
}
