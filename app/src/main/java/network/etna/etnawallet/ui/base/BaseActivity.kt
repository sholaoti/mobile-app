package network.etna.etnawallet.ui.base

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import network.etna.etnawallet.R
import network.etna.etnawallet.repository.pincode.PinCodeRepository
import network.etna.etnawallet.repository.refresh.RefreshRepository
import network.etna.etnawallet.repository.warningservice.WarningService
import network.etna.etnawallet.ui.base.warning.WarningView
import network.etna.etnawallet.ui.verifypin.VerifyPinActivity
import org.koin.android.ext.android.inject

open class BaseActivity: AppCompatActivity() {

    private val warningService: WarningService by inject()
    private val pinCodeRepository: PinCodeRepository by inject()
    private val refreshRepository: RefreshRepository by inject()

    private var composite: CompositeDisposable? = null

    open val allowBackAction: Boolean = true

    override fun onBackPressed() {
        if (!allowBackAction) {
            return
        }

        if (onBackPressedDispatcher.hasEnabledCallbacks()) {
            onBackPressedDispatcher.onBackPressed()
        } else if (!isTaskRoot) {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()

        if (pinCodeRepository.getNeedsPinCode()) {
            pinCodeRepository.setNeedsPinCode(false)

            if (pinCodeRepository.isPinCodeVerified()) {
                val intent = Intent(this, VerifyPinActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }

        }

        try {
            val swipeRefreshLayout: SwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
            swipeRefreshLayout.setOnRefreshListener {
                refreshRepository.startRefresh()
            }
        } catch (e: Exception) {}

    }

    override fun onResume() {
        super.onResume()

        composite?.dispose()
        composite = CompositeDisposable()

        composite?.add(
        warningService
            .getWarningObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                try {
                    val warningView = findViewById<WarningView>(R.id.warningView)

                    if (it.isPresent) {
                        val text = it.get()
                        warningView.binding.warningTextView.text = text
                        warningView.binding.warningMotionLayout.transitionToState(R.id.warningLayoutVisible)
                    } else {
                        warningView.binding.warningMotionLayout.transitionToState(R.id.warningLayoutHidden)
                    }
                } catch (e: Exception) {}
            }
        )

        composite?.add(
            warningService
                .getErrorObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {

                    try {
                        val warningView = findViewById<WarningView>(R.id.warningView)

                        if (it.isPresent) {
                            val text = it.get()
                            warningView.binding.errorTextView.text = text
                            warningView.binding.errorMotionLayout.transitionToState(R.id.errorLayoutVisible)
                        } else {
                            warningView.binding.errorMotionLayout.transitionToState(R.id.errorLayoutHidden)
                        }
                    } catch (e: Exception) {}
                }
        )

        composite?.add(
            refreshRepository
                .getRefreshingObservable()
                .subscribe {
                    try {
                        val swipeRefreshLayout: SwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
                        swipeRefreshLayout.isRefreshing = it
                    } catch (e: Exception) {}
                }
        )

    }

    override fun onPause() {
        super.onPause()
        composite?.dispose()
        composite = null
    }

    fun setSwipeRefreshEnabled(value: Boolean) {
        try {
            val swipeRefreshLayout: SwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
            swipeRefreshLayout.isEnabled = value
        } catch (e: Exception) {}
    }
}