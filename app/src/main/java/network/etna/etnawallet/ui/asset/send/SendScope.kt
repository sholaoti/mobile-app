package network.etna.etnawallet.ui.asset.send

import network.etna.etnawallet.ui.asset.send.selecttoken.SelectTokenViewModel
import network.etna.etnawallet.ui.asset.send.sendform.SendFormViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val sendModule = module {
    single { SendScenarioModel() }
    viewModel { SendFormViewModel() }
    viewModel { SelectTokenViewModel() }
}