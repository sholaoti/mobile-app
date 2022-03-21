package network.etna.etnawallet.ui.addwallet

import android.content.Intent
import android.os.Bundle
import network.etna.etnawallet.MainActivity
import network.etna.etnawallet.R
import network.etna.etnawallet.ui.base.BaseActivity
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

class ImportWalletActivity : BaseActivity() {

    private val scenarioModel: ImportWalletScenarioModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadKoinModules(importWalletScope)

        setContentView(R.layout.activity_import_wallet)
    }

    override fun finish() {
        if (scenarioModel.navigateToMain) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            super.finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
        } else {
            super.finish()
            if (scenarioModel.overrideActivityFinishAnimation) {
                overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out)
            }
        }

        unloadKoinModules(importWalletScope)
    }
}