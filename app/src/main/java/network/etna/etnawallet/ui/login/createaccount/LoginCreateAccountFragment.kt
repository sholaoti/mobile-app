package network.etna.etnawallet.ui.login.createaccount

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.navigation.fragment.findNavController
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentLoginCreateAccountBinding
import network.etna.etnawallet.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LoginCreateAccountFragment :
    BaseFragment<FragmentLoginCreateAccountBinding>(
        FragmentLoginCreateAccountBinding::inflate,
        R.string.create_account
    ) {

    val viewModel: LoginCreateAccountByEmailViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.termsTextView.movementMethod = LinkMovementMethod.getInstance()

        binding.createAccountButton.setOnClickListener {
            val email = binding.emailTextField.text.toString()
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                viewModel.email = email
                //findNavController().navigate(LoginCreateAccountFragmentDirections.toLoginCheckEmailFragment())
            }
        }
    }
}