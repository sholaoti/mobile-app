package network.etna.etnawallet.ui.login

import android.os.Bundle
import network.etna.etnawallet.R
import network.etna.etnawallet.ui.base.BaseActivity

class LoginActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    override fun finish() {
        super.finish()
        //overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out)
    }
}