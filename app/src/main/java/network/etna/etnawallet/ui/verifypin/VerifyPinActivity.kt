package network.etna.etnawallet.ui.verifypin

import android.os.Bundle
import network.etna.etnawallet.R
import network.etna.etnawallet.ui.base.BaseActivity

class VerifyPinActivity : BaseActivity() {

    override val allowBackAction: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_verify_pin)
    }
}