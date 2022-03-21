package network.etna.etnawallet.ui.login.pincode.confirmpincode

import androidx.navigation.fragment.navArgs
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.Observable
import network.etna.etnawallet.R
import network.etna.etnawallet.ui.login.LoginStateViewModel
import network.etna.etnawallet.ui.login.pincode.PinCodeFragment
import network.etna.etnawallet.ui.login.pincode.PinCodeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ConfirmPinCodeFragment: PinCodeFragment() {

    private val args: ConfirmPinCodeFragmentArgs by navArgs()
    private val viewModel: ConfirmPinCodeViewModel by viewModel{ parametersOf(args.pinCode) }
    private val loginStateViewModel: LoginStateViewModel by viewModel()

    override fun getViewModel(): PinCodeViewModel {
        return viewModel
    }

    override fun getTitleText(): String {
        return resources.getString(R.string.confirm_pin)
    }

    override fun getSubtitleTextObservable(): Observable<String> {
        return Observable.just(resources.getString(R.string.confirm_pin_subtitle))
    }

    override fun pinCodeSuccess() {
        loginStateViewModel
            .goToNextState(this)
            .disposedWith(viewLifecycleOwner)
    }
}