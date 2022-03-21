package network.etna.etnawallet.ui.recoveryphrase

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.navigation.navArgs
import network.etna.etnawallet.MainActivity
import network.etna.etnawallet.R
import network.etna.etnawallet.ui.base.BaseActivity
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

class RecoveryPhraseActivity : BaseActivity() {

    private val args: RecoveryPhraseActivityArgs by navArgs()
    private val scenarioModel: RecoveryPhraseScenarioModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadKoinModules(recoveryPhraseModule)

        scenarioModel.walletId = args.walletId
        scenarioModel.checkOTP = args.checkOTP

        setContentView(R.layout.activity_recovery_phrase)
    }

    override fun finish() {
        if (isTaskRoot) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            super.finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
        } else {
            super.finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out)
        }

        unloadKoinModules(recoveryPhraseModule)
    }
}