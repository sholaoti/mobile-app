package network.etna.etnawallet.ui.addwallet.importsinglewallet.variants.address

import android.os.Bundle
import android.view.View
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentImportSingleWalletAddressBinding
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.helpers.extensions.afterTextChanged
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.androidx.viewmodel.ext.android.viewModel

class ImportSingleWalletAddressFragment :
    BaseFragment<FragmentImportSingleWalletAddressBinding>(
        FragmentImportSingleWalletAddressBinding::inflate
    ) {
    private val viewModel: ImportSingleWalletAddressViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.walletAddressDesc.text = resources.getString(R.string.import_single_wallet_address_variant, viewModel.getPlatformName())

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

        binding.walletAddressTextField.afterTextChanged { viewModel.walletAddressChanged(it) }
    }
}