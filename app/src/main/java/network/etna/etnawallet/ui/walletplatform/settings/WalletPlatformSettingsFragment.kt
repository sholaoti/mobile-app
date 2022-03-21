package network.etna.etnawallet.ui.walletplatform.settings

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentWalletPlatformSettingsBinding
import network.etna.etnawallet.repository.wallets.cryptowallet.model.ERCTokenType
import network.etna.etnawallet.repository.warningservice.subscribeWithErrorHandler
import network.etna.etnawallet.sdk.api.ImageProvider
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.helpers.extensions.dpToPx
import network.etna.etnawallet.ui.helpers.extensions.toggleVisibilityGone
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalletPlatformSettingsFragment :
    BaseFragment<FragmentWalletPlatformSettingsBinding>(
        FragmentWalletPlatformSettingsBinding::inflate,
        R.string.wallet_platform_add_token
    ) {

    private val viewModel: WalletPlatformSettingsViewModel by viewModel()

    private val imageSize: Int by lazy {
        requireContext().dpToPx(24)
    }

    override fun getTitleObservable(): Observable<String> {
        return viewModel
            .getWalletNameObservable()
            .map {
                resources.getString(R.string.wallet_platform_settings_title, it)
            }
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

                model.supportedTokens.any { it == ERCTokenType.erc20 }.apply {
                    binding.addTokenTextView.toggleVisibilityGone(this)
                }

                model.supportedTokens.any { it == ERCTokenType.erc721 }.apply {
                    binding.addNftTextView.toggleVisibilityGone(this)
                }

                model.supportedTokens.isNotEmpty().apply {
                    binding.tokensListTextView.toggleVisibilityGone(this)
                }
            }
            .disposedWith(viewLifecycleOwner)

        binding.tokensListTextView.setOnClickListener {
            findNavController().navigate(WalletPlatformSettingsFragmentDirections.toWalletPlatformTokenListFragment())
        }

        binding.addTokenTextView.setOnClickListener {
            findNavController().navigate(WalletPlatformSettingsFragmentDirections.toWalletPlatformAddTokenFragment())
        }

        binding.addNftTextView.setOnClickListener {
            findNavController().navigate(WalletPlatformSettingsFragmentDirections.toWalletPlatformAddNFTTokenFragment())
        }
    }
}