package network.etna.etnawallet.ui.login.start

import android.os.Bundle
import android.view.View
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.lock.AuthenticationCallback
import com.auth0.android.lock.InitialScreen
import com.auth0.android.lock.Lock
import com.auth0.android.lock.PasswordlessLock
import com.auth0.android.result.Authentication
import com.auth0.android.result.Credentials
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentLoginStartBinding
import network.etna.etnawallet.repository.auth0.Auth0Repository
import network.etna.etnawallet.ui.base.BaseFragment
import org.koin.android.ext.android.inject

class LoginStartFragment :
    BaseFragment<FragmentLoginStartBinding>(
        FragmentLoginStartBinding::inflate,
        R.string.welcome
    ) {

    private val auth0Repository: Auth0Repository by inject()

    private val callback = object : AuthenticationCallback() {
        override fun onAuthentication(credentials: Credentials) {
            // Authenticated
            auth0Repository.updateCredentials(credentials)
            pendingDirection = LoginStartFragmentDirections.toLoginSplashFragment()
        }

        override fun onError(error: AuthenticationException) {
            // Exception occurred
            val x = 1
        }
    }

    private var pendingDirection: NavDirections? = null

    override fun onResume() {
        super.onResume()
        pendingDirection?.let {
            findNavController().navigate(it)
            pendingDirection = null
        }
    }

    private lateinit var lock: Lock

    private fun startLock(initialScreen: Int) {
        val account = Auth0(requireContext())
        lock = Lock.newBuilder(account, callback)
            .withScope("offline_access openid profile email")
            .withAudience("https://login-wallet.us.auth0.com/api/v2/")
            .initialScreen(initialScreen)
            .closable(true)
            .build(requireContext())
        startActivity(lock.newIntent(requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startButton.setOnClickListener {
            startLock(InitialScreen.SIGN_UP)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        lock.onDestroy(requireContext())
    }
}