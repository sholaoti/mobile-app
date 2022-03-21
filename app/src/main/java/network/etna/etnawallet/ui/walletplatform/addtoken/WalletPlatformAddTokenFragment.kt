package network.etna.etnawallet.ui.walletplatform.addtoken

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.android.schedulers.AndroidSchedulers
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentWalletPlatformAddTokenBinding
import network.etna.etnawallet.repository.wallets.etnawallet.toJsonString
import network.etna.etnawallet.repository.warningservice.subscribeWithErrorHandler
import network.etna.etnawallet.sdk.api.ImageProvider
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.helpers.extensions.afterTextChanged
import network.etna.etnawallet.ui.helpers.extensions.dpToPx
import network.etna.etnawallet.ui.walletplatform.searchtoken.WalletPlatformSearchTokenViewModelInput
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalletPlatformAddTokenFragment :
    BaseFragment<FragmentWalletPlatformAddTokenBinding>(
        FragmentWalletPlatformAddTokenBinding::inflate,
        R.string.wallet_platform_add_token_title
    ) {

    private val viewModel: WalletPlatformAddTokenViewModel by viewModel()

    private val imageSize: Int by lazy {
        requireContext().dpToPx(24)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel
            .getPlatformObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWithErrorHandler { model ->
                Glide
                    .with(requireContext())
                    .load(ImageProvider.getPlatformImageURL(model.id, imageSize))
                    .into(binding.platformImageView)
                binding.platformName.text = resources.getString(R.string.wallet_platform_settings_platform_name, model.name, model.symbol)
            }
            .disposedWith(viewLifecycleOwner)

        binding.tokenAddressTextField.afterTextChanged { viewModel.addressChanged(it) }

        viewModel
            .isAddTokenAvailable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.addButton.isEnabled = it
            }
            .disposedWith(viewLifecycleOwner)

        binding.addButton.bind(
            {
                viewModel.checkAddressValid()
            },
            viewLifecycleOwner,
            {
                val input = WalletPlatformSearchTokenViewModelInput(viewModel.getAddress(), null)
                findNavController().navigate(
                    WalletPlatformAddTokenFragmentDirections
                        .toWalletPlatformSearchTokenFragment(input.toJsonString())
                )
            }
        )

        //0x51f35073ff7cf54c9e86b7042e59a8cc9709fc46 - ETNA token
        //0x52B31a9Fa4D1Ad6BDf6AA1EE30a5Dfe8AbD210E8 - test token address
    }
}