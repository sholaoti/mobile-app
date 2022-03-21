package network.etna.etnawallet.ui.addwallet.importsinglewallet.variants.privatekey

import android.os.Bundle
import android.view.View
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.android.schedulers.AndroidSchedulers
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentImportSingleWalletPrivateKeyBinding
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.helpers.extensions.afterTextChanged
import org.koin.androidx.viewmodel.ext.android.viewModel

class ImportSingleWalletPrivateKeyFragment :
    BaseFragment<FragmentImportSingleWalletPrivateKeyBinding>(
    FragmentImportSingleWalletPrivateKeyBinding::inflate
) {

    private val viewModel: ImportSingleWalletPrivateKeyViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.walletPrivateKeyDesc.text = resources.getString(R.string.import_single_wallet_address_variant, viewModel.getPlatformName())

        binding.importButton.isEnabled = false

        viewModel
            .getImportAvailableObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.importButton.isEnabled = it
            }.disposedWith(viewLifecycleOwner)

        binding.importButton.setOnClickListener {
            binding.importButton.isEnabled = false
            viewModel.importPressed()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        activity?.finish()
                    },
                    {
                        binding.importButton.isEnabled = true
                    }
                ).disposedWith(viewLifecycleOwner)
        }

        binding.walletNameTextField.afterTextChanged { viewModel.walletNameChanged(it) }

        binding.walletPrivateKeyTextField.afterTextChanged { viewModel.walletPrivateKeyChanged(it) }
    }
}