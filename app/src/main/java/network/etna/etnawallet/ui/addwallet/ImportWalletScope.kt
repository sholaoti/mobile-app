package network.etna.etnawallet.ui.addwallet

import network.etna.etnawallet.ui.addwallet.createmultiwallet.CreateMultiWalletViewModel
import network.etna.etnawallet.ui.addwallet.importmultiwallet.ImportMultiWalletViewModel
import network.etna.etnawallet.ui.addwallet.importsinglewallet.ImportSingleWalletScenarioModel
import network.etna.etnawallet.ui.addwallet.importsinglewallet.ImportSingleWalletViewModel
import network.etna.etnawallet.ui.addwallet.importsinglewallet.variants.address.ImportSingleWalletAddressViewModel
import network.etna.etnawallet.ui.addwallet.importsinglewallet.variants.privatekey.ImportSingleWalletPrivateKeyViewModel
import network.etna.etnawallet.ui.addwallet.importsinglewallet.variants.seed.ImportSingleWalletSeedViewModel
import network.etna.etnawallet.ui.addwallet.importwallet.ImportWalletViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val importWalletScope = module {
    single { ImportWalletScenarioModel() }
    single { ImportSingleWalletScenarioModel() }
    viewModel { CreateMultiWalletViewModel() }
    viewModel { ImportMultiWalletViewModel() }
    viewModel { ImportWalletViewModel() }
    viewModel { ImportSingleWalletViewModel() }
    viewModel { ImportSingleWalletAddressViewModel() }
    viewModel { ImportSingleWalletSeedViewModel() }
    viewModel { ImportSingleWalletPrivateKeyViewModel() }
}