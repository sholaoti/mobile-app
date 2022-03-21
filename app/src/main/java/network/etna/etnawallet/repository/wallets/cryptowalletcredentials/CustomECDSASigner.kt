package network.etna.etnawallet.repository.wallets.cryptowalletcredentials

import network.etna.etnawallet.extensions.toHexString
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.crypto.params.ECPrivateKeyParameters
import org.bouncycastle.crypto.signers.HMacDSAKCalculator
import org.bouncycastle.math.ec.FixedPointCombMultiplier
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Sign
import java.math.BigInteger

class CustomECDSASigner {

    companion object {
        private val CURVE = ECDomainParameters(
            Sign.CURVE_PARAMS.curve,
            Sign.CURVE_PARAMS.g,
            Sign.CURVE_PARAMS.n,
            Sign.CURVE_PARAMS.h)

        private val HALF_CURVE_ORDER = Sign.CURVE_PARAMS.n.shiftRight(1)
    }

    fun sign(message: ByteArray, keypair: ECKeyPair): Signature {
        val key = ECPrivateKeyParameters(keypair.privateKey, CURVE)

        val ec = key.parameters
        val n = ec.n
        val e = calculateE(n, message)
        val d = key.d

        val kCalculator = HMacDSAKCalculator(SHA256Digest())

        kCalculator.init(n, d, message)

        var r: BigInteger
        var s: BigInteger

        val basePointMultiplier = FixedPointCombMultiplier()

        do {
            var k: BigInteger
            do {
                k = kCalculator.nextK()
                val p = basePointMultiplier.multiply(ec.g, k).normalize()
                r = p.affineXCoord.toBigInteger().mod(n)
            } while (r == BigInteger.ZERO)

            s = k.modInverse(n).multiply(e.add(d.multiply(r))).mod(n)

        } while (s == BigInteger.ZERO)

        if (s > HALF_CURVE_ORDER) {
            s = Sign.CURVE_PARAMS.n.subtract(s)
        }

        var rHex = r.toByteArray().toHexString()
        var sHex = s.toByteArray().toHexString()

        if (rHex.startsWith("00")) {
            rHex = rHex.drop(2)
        }

        if (sHex.startsWith("00")) {
            sHex = sHex.drop(2)
        }

        return Signature(
            rHex,
            sHex
        )
    }

    private fun calculateE(n: BigInteger, message: ByteArray): BigInteger {
        val log2n = n.bitLength()
        val messageBitLength = message.size * 8

        var e = BigInteger(1, message)

        if (log2n < messageBitLength) {
            e = e.shiftRight(messageBitLength - log2n)
        }

        return e
    }
}