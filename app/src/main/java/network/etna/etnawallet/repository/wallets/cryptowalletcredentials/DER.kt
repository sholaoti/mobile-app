package network.etna.etnawallet.repository.wallets.cryptowalletcredentials

import network.etna.etnawallet.extensions.toHexString
import org.web3j.crypto.ECDSASignature
import java.math.BigInteger

fun ECDSASignature.toDer() : String? {

    if (r == BigInteger.ZERO || s == BigInteger.ZERO) {
        return null
    }

    val order = BigInteger("ffffffff00000000ffffffffffffffffbce6faada7179e84f3b9cac2fc632551", 16)
    val halfOrder = order.shr(1)

    var bcS = s

    if (bcS > halfOrder) {
        bcS = order - bcS
    }

    var rBytes = r.toByteArray()
    var sBytes = bcS.toByteArray()

    if (rBytes.first().toInt() >= 128) {
        rBytes = ByteArray(1) { 0 } + rBytes
    }

    if (sBytes.first().toInt() >= 128) {
        sBytes = ByteArray(1) { 0 } + sBytes
    }

    val _der : ByteArray = byteArrayOf(2, rBytes.size.toByte()) + rBytes + byteArrayOf(2, sBytes.size.toByte()) + sBytes
    val der : ByteArray = byteArrayOf(48, _der.size.toByte()) + _der

    return der.toHexString()
}