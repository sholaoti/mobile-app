package network.etna.etnawallet.ui.login.splash

import android.os.Bundle
import android.view.View
import com.kizitonwose.android.disposebag.disposedWith
import network.etna.etnawallet.databinding.FragmentLoginSplashBinding
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.login.LoginStateViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginSplashFragment :
    BaseFragment<FragmentLoginSplashBinding>(
        FragmentLoginSplashBinding::inflate
    ) {

    val viewModel: LoginStateViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel
            .goToNextState(this)
            .disposedWith(viewLifecycleOwner)
    }
}