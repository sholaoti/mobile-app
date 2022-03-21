package network.etna.etnawallet.ui.login.checkemail

import android.os.Bundle
import android.view.View
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentLoginCheckEmailBinding
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.login.LoginStateViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class LoginCheckEmailFragment :
    BaseFragment<FragmentLoginCheckEmailBinding>(
        FragmentLoginCheckEmailBinding::inflate,
        R.string.check_your_email
    ) {

    private val viewModel: LoginCheckEmailViewModel by viewModel()
    private val loginStateViewModel: LoginStateViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getUserInfoObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    binding.emailTextView.text = it.email
                    if (it.email_verified) {
                        binding.motionLayout.transitionToEnd()
                    } else {
                        binding.motionLayout.transitionToStart()
                    }
                },
                {})
            .disposedWith(viewLifecycleOwner)

        Observable
            .interval(15, TimeUnit.SECONDS)
            .subscribe {
                viewModel.refreshUserInfo()
            }
            .disposedWith(viewLifecycleOwner)

        binding.continueButton.setOnClickListener {
            goToNextScreen()
        }
    }

    fun goToNextScreen() {
        loginStateViewModel
            .goToNextState(this)
            .disposedWith(viewLifecycleOwner)
    }
}