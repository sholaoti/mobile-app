package network.etna.etnawallet.ui.recoveryphrase

import network.etna.etnawallet.ui.recoveryphrase.initial.RecoveryPhraseInitialViewModel
import network.etna.etnawallet.ui.recoveryphrase.show.RecoveryPhraseShowViewModel
import network.etna.etnawallet.ui.recoveryphrase.verify.RecoveryPhraseVerifyViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val recoveryPhraseModule = module {
    single { RecoveryPhraseScenarioModel() }
    viewModel { RecoveryPhraseInitialViewModel() }
    viewModel { RecoveryPhraseShowViewModel() }
    viewModel { RecoveryPhraseVerifyViewModel() }
}