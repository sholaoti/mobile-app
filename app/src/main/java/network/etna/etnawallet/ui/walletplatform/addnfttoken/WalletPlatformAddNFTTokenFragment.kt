package network.etna.etnawallet.ui.walletplatform.addnfttoken

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.android.schedulers.AndroidSchedulers
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentWalletPlatformAddNftTokenBinding
import network.etna.etnawallet.repository.wallets.etnawallet.toJsonString
import network.etna.etnawallet.repository.warningservice.subscribeWithErrorHandler
import network.etna.etnawallet.sdk.api.ImageProvider
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.helpers.extensions.afterTextChanged
import network.etna.etnawallet.ui.helpers.extensions.dpToPx
import network.etna.etnawallet.ui.walletplatform.addtoken.WalletPlatformAddTokenFragmentDirections
import network.etna.etnawallet.ui.walletplatform.searchtoken.WalletPlatformSearchTokenViewModelInput
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalletPlatformAddNFTTokenFragment :
    BaseFragment<FragmentWalletPlatformAddNftTokenBinding>(
        FragmentWalletPlatformAddNftTokenBinding::inflate,
        R.string.wallet_platform_add_nft_token_title
    ) {

    private val viewModel: WalletPlatformAddNFTTokenViewModel by viewModel()

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

        binding.nftIdTextField.afterTextChanged { viewModel.nftIdChanged(it) }

        binding.nftAddressTextField.afterTextChanged { viewModel.nftAddressChanged(it) }

        viewModel
            .isAddTokenAvailable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.addButton.isEnabled = it
            }
            .disposedWith(viewLifecycleOwner)

        binding.addButton.setOnClickListener {
            try {
                val input = WalletPlatformSearchTokenViewModelInput(viewModel.getNftAddress(), viewModel.getNftId())
                findNavController().navigate(
                    WalletPlatformAddTokenFragmentDirections
                        .toWalletPlatformSearchTokenFragment(input.toJsonString())
                )
            } catch (e: Exception) {}
        }

        //0xd34Eb2d530245a60C6151B6cfa6D247Ee92668c7 1803
    }
}