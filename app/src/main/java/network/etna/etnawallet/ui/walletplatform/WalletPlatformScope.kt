package network.etna.etnawallet.ui.walletplatform

import android.content.Context
import network.etna.etnawallet.ui.walletplatform.addnfttoken.WalletPlatformAddNFTTokenViewModel
import network.etna.etnawallet.ui.walletplatform.addtoken.WalletPlatformAddTokenViewModel
import network.etna.etnawallet.ui.walletplatform.searchtoken.WalletPlatformSearchNFTTokenViewModel
import network.etna.etnawallet.ui.walletplatform.searchtoken.WalletPlatformSearchTokenViewModel
import network.etna.etnawallet.ui.walletplatform.searchtoken.WalletPlatformSearchTokenViewModelInput
import network.etna.etnawallet.ui.walletplatform.searchtoken.WalletPlatformSearchTokenViewModelInterface
import network.etna.etnawallet.ui.walletplatform.settings.WalletPlatformSettingsViewModel
import network.etna.etnawallet.ui.walletplatform.tokenlist.WalletPlatformTokenListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val walletPlatformModule = module {
    viewModel { WalletPlatformSettingsViewModel() }
    viewModel { WalletPlatformTokenListViewModel() }
    viewModel { WalletPlatformAddTokenViewModel() }
    viewModel { WalletPlatformAddNFTTokenViewModel() }
    
    viewModel<WalletPlatformSearchTokenViewModelInterface> { (input: WalletPlatformSearchTokenViewModelInput) ->
        if (input.nftId != null) {
            WalletPlatformSearchNFTTokenViewModel(get<Context>().resources, input)
        } else {
            WalletPlatformSearchTokenViewModel(get<Context>().resources, input)
        }
    }
}