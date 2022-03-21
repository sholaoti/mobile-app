package network.etna.etnawallet.ui.login.createaccount

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.android.schedulers.AndroidSchedulers
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentLoginCreatePasswordBinding
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.base.buttons.MainButtonState
import network.etna.etnawallet.ui.helpers.extensions.afterTextChanged
import network.etna.etnawallet.ui.helpers.extensions.toggleVisibility
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LoginCreatePasswordFragment :
    BaseFragment<FragmentLoginCreatePasswordBinding>(
        FragmentLoginCreatePasswordBinding::inflate,
        R.string.create_password
    ) {

    val viewModel: LoginCreateAccountByEmailViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.passwordTextField.afterTextChanged { viewModel.passwordChanged(it) }
        binding.confirmPasswordTextField.afterTextChanged { viewModel.confirmPasswordChanged(it) }

        viewModel.passwordValid()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.passwordCheckImageView.toggleVisibility(it)
            }
            .disposedWith(viewLifecycleOwner)

        viewModel.confirmPasswordValid()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.confirmPasswordCheckImageView.toggleVisibility(it)
            }
            .disposedWith(viewLifecycleOwner)

        binding.primaryButton.setOnClickListener {
            if (binding.primaryButton.state == MainButtonState.NORMAL) {
                binding.primaryButton.state = MainButtonState.LOADING

                Handler(Looper.getMainLooper()).postDelayed({
                    binding.primaryButton.state = MainButtonState.SUCCESS
                }, 5000)
            }
        }
    }
}