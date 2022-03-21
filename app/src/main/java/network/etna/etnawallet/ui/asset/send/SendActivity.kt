package network.etna.etnawallet.ui.asset.send

import android.os.Bundle
import androidx.navigation.navArgs
import network.etna.etnawallet.R
import network.etna.etnawallet.repository.wallets.etnawallet.AssetIdHolder
import network.etna.etnawallet.repository.wallets.etnawallet.fromJsonString
import network.etna.etnawallet.ui.base.BaseActivity
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

class SendActivity : BaseActivity() {

    private val args: SendActivityArgs by navArgs()
    private val scenarioModel: SendScenarioModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadKoinModules(sendModule)

        scenarioModel.updateAsset(args.assetIdHolder.fromJsonString(AssetIdHolder::class.java)!!)

        setContentView(R.layout.activity_send)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out)
        unloadKoinModules(sendModule)
    }
}