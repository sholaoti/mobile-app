package network.etna.etnawallet.extensions

import kotlin.experimental.and

fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }
fun ByteArray.toBinaryString() = joinToString("") { it.toBinaryString() }

fun Byte.toBinaryString(): String {
    val masks = byteArrayOf(-128, 64, 32, 16, 8, 4, 2, 1)
    val builder = StringBuilder()

    for (m in masks) {
        if ((this and m) == m) {
            builder.append('1')
        } else {
            builder.append('0')
        }
    }

    return builder.toString()
}
