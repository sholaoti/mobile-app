package network.etna.etnawallet.ui.login.pincode.createpincode

import androidx.navigation.fragment.findNavController
import io.reactivex.Observable
import network.etna.etnawallet.R
import network.etna.etnawallet.ui.login.pincode.PinCodeFragment
import network.etna.etnawallet.ui.login.pincode.PinCodeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePinCodeFragment: PinCodeFragment() {

    private val viewModel: CreatePinCodeViewModel by viewModel()

    override fun getViewModel(): PinCodeViewModel {
        return viewModel
    }

    override fun getTitleText(): String {
        return resources.getString(R.string.create_pin)
    }

    override fun getSubtitleTextObservable(): Observable<String> {
        return Observable.just(resources.getString(R.string.create_pin_subtitle))
    }

    override fun pinCodeSuccess() {
        findNavController()
            .navigate(
                CreatePinCodeFragmentDirections.toLoginConfirmPinCodeFragment(
                    viewModel.getPinCode()
                )
            )
    }
}