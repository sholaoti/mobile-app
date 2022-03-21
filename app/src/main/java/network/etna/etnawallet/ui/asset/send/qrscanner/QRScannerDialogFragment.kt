package network.etna.etnawallet.ui.asset.send.qrscanner

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentQrScannerDialogBinding
import network.etna.etnawallet.ui.asset.send.SendScenarioModel
import network.etna.etnawallet.ui.base.BaseModalDialogFragment
import org.koin.android.ext.android.inject

class QRScannerDialogFragment: BaseModalDialogFragment<FragmentQrScannerDialogBinding>(
    FragmentQrScannerDialogBinding::inflate,
    R.string.qr_scanner_dialog_title
) {

    private lateinit var beepManager: BeepManager
    private val sendScenarioModel: SendScenarioModel by inject()

    private val callback: BarcodeCallback = BarcodeCallback {
        sendScenarioModel.updateRecipientAddress(it.text)
        findNavController().popBackStack()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val formats: List<BarcodeFormat> = listOf(BarcodeFormat.QR_CODE)

        binding.barcodeScanner.decoderFactory = DefaultDecoderFactory(formats)
        binding.barcodeScanner.decodeSingle(callback)

        beepManager = BeepManager(requireActivity())
    }

    override fun onResume() {
        super.onResume()
        binding.barcodeScanner.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.barcodeScanner.pause()
    }

}