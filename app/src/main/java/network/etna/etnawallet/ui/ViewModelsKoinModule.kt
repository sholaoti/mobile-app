package network.etna.etnawallet.ui

import com.auth0.android.Auth0
import network.etna.etnawallet.ui.general.GeneralFragmentViewModel
import network.etna.etnawallet.ui.general.selectwallet.SelectWalletViewModel
import network.etna.etnawallet.ui.login.LoginStateViewModel
import network.etna.etnawallet.ui.login.biometry.ActivateBiometryViewModel
import network.etna.etnawallet.ui.login.checkemail.LoginCheckEmailViewModel
import network.etna.etnawallet.ui.login.createaccount.LoginCreateAccountByEmailViewModel
import network.etna.etnawallet.ui.login.pincode.checkpincode.CheckPinCodeViewModel
import network.etna.etnawallet.ui.login.pincode.confirmpincode.ConfirmPinCodeViewModel
import network.etna.etnawallet.ui.login.pincode.createpincode.CreatePinCodeViewModel
import network.etna.etnawallet.ui.login.pincode.otp.OTPViewModel
import network.etna.etnawallet.ui.profile.settings.ProfileSettingsViewModel
import network.etna.etnawallet.ui.profile.settings.activenetwork.ProfileSettingsActiveNetworkViewModel
import network.etna.etnawallet.ui.profile.settings.activenetwork.testnetwarning.TestnetWarningViewModel
import network.etna.etnawallet.ui.profile.settings.currency.ProfileSettingsCurrencyViewModel
import network.etna.etnawallet.ui.profile.wallets.changewalletname.ChangeWalletNameViewModel
import network.etna.etnawallet.ui.profile.wallets.walletinfo.ProfileWalletInfoViewModel
import network.etna.etnawallet.ui.profile.walletslist.ProfileWalletsListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {

    viewModel { GeneralFragmentViewModel() }

    viewModel { (wallet_id: String) -> ProfileWalletInfoViewModel(wallet_id) }
    viewModel { (wallet_id: String) -> ChangeWalletNameViewModel(wallet_id) }
    viewModel { SelectWalletViewModel() }
    viewModel { ProfileWalletsListViewModel() }

    viewModel { ProfileSettingsViewModel() }
    viewModel { ProfileSettingsCurrencyViewModel() }
    viewModel { ProfileSettingsActiveNetworkViewModel() }
    viewModel { TestnetWarningViewModel() }

    viewModel { LoginCreateAccountByEmailViewModel() }
    viewModel { LoginCheckEmailViewModel() }

    viewModel { CreatePinCodeViewModel() }
    viewModel { (pinCode: String) -> ConfirmPinCodeViewModel(pinCode) }
    viewModel { CheckPinCodeViewModel() }

    viewModel { ActivateBiometryViewModel() }
    viewModel { LoginStateViewModel() }

    viewModel { OTPViewModel(Auth0(get())) }
}
