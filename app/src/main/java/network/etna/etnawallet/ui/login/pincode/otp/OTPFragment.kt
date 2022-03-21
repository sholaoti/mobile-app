package network.etna.etnawallet.ui.login.pincode.otp

import androidx.navigation.fragment.findNavController
import io.reactivex.Observable
import network.etna.etnawallet.R
import network.etna.etnawallet.ui.login.pincode.PinCodeFragment
import network.etna.etnawallet.ui.login.pincode.PinCodeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class OTPFragment: PinCodeFragment() {

    private val viewModel: OTPViewModel by viewModel()

    override fun getViewModel(): PinCodeViewModel {
        return viewModel
    }

    override fun getTitleText(): String {
        return resources.getString(R.string.otp_fragment_title)
    }

    override fun getSubtitleTextObservable(): Observable<String> {
        return viewModel
            .getTriesLeftObservable()
            .doOnNext {
                if (it == 0) {
                    findNavController().popBackStack()
                }
            }
            .map {
                resources.getString(R.string.otp_fragment_subtitle, viewModel.userEmail, it.toString())
            }
    }

    override fun pinCodeSuccess() {
        findNavController().navigate(OTPFragmentDirections.toRecoveryPhraseShowFragment())
    }
}