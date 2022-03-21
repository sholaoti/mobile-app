package network.etna.etnawallet.ui.asset

import android.os.Bundle
import androidx.navigation.navArgs
import network.etna.etnawallet.R
import network.etna.etnawallet.repository.wallets.etnawallet.AssetIdHolder
import network.etna.etnawallet.ui.base.BaseActivity
import network.etna.etnawallet.ui.walletplatform.walletPlatformModule
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

class AssetActivity : BaseActivity() {

    private val args: AssetActivityArgs by navArgs()
    private val scenarioModel: AssetScenarioModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadKoinModules(assetModule)
        loadKoinModules(walletPlatformModule)

        scenarioModel.assetIdHolder = AssetIdHolder(
            args.isCurrency,
            args.platform,
            args.address,
            args.assetId,
            args.referenceAssetId
        )

        setContentView(R.layout.activity_asset)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out)
        unloadKoinModules(assetModule)
        unloadKoinModules(walletPlatformModule)
    }
}