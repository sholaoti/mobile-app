package network.etna.etnawallet.ui.profile.wallets.delete

import android.os.Bundle
import androidx.navigation.navArgs
import network.etna.etnawallet.R
import network.etna.etnawallet.ui.base.BaseActivity
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

class DeleteWalletActivity: BaseActivity() {

    private val args: DeleteWalletActivityArgs by navArgs()
    private val scenarioModel: DeleteWalletScenarioModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadKoinModules(deleteWalletModule)

        scenarioModel.walletId = args.walletId

        setContentView(R.layout.activity_delete_wallet)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out)
        unloadKoinModules(deleteWalletModule)
    }

    override fun onBackPressed() {
        finish()
    }
}