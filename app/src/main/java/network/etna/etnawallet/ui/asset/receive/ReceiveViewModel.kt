package network.etna.etnawallet.ui.asset.receive

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.ViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import network.etna.etnawallet.ui.asset.AssetScenarioModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ReceiveViewModel: ViewModel(), KoinComponent {

    private val scenarioModel: AssetScenarioModel by inject()

    fun getWalletAddress(): String {
        return scenarioModel.walletAddress
    }

    fun getQrCodeBitmap(size: Int): Bitmap {
        val hints = hashMapOf<EncodeHintType, Int>().also { it[EncodeHintType.MARGIN] = 1 } // Make the QR code buffer border narrower
        val bits = QRCodeWriter().encode(getWalletAddress(), BarcodeFormat.QR_CODE, size, size, hints)
        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
    }
}