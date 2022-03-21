package network.etna.etnawallet.ui.profile.wallets.delete

import network.etna.etnawallet.ui.profile.wallets.delete.confirmation.DeleteWalletConfirmationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val deleteWalletModule = module {
    single { DeleteWalletScenarioModel() }
    viewModel { DeleteWalletConfirmationViewModel() }
}