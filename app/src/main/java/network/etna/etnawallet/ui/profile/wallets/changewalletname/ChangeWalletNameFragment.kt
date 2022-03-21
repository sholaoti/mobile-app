package network.etna.etnawallet.ui.profile.wallets.changewalletname

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.android.schedulers.AndroidSchedulers
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentProfileWalletChangeNameBinding
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.helpers.extensions.afterTextChanged
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ChangeWalletNameFragment :
    BaseFragment<FragmentProfileWalletChangeNameBinding>(
        FragmentProfileWalletChangeNameBinding::inflate,
        R.string.change_wallet_name
    ) {

    private val args: ChangeWalletNameFragmentArgs by navArgs()
    private val viewModel: ChangeWalletNameViewModel by viewModel { parametersOf(args.walletId) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel
            .isSaveAllowed()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.saveButton.isEnabled = it
            }.disposedWith(viewLifecycleOwner)

        viewModel
            .getWalletName()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                binding.walletNameTextField.setText(it)
                },
                {

                }
            ).disposedWith(viewLifecycleOwner)

        binding.walletNameTextField.afterTextChanged { viewModel.walletNameChanged(it) }

        binding.saveButton.setOnClickListener {
            viewModel.setupName()
            findNavController().popBackStack()
        }
    }
}