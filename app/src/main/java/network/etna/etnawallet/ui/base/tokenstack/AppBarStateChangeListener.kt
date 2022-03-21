package network.etna.etnawallet.ui.base.tokenstack

import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

enum class AppBarState {
    EXPANDED, COLLAPSED, IDLE
}

abstract class AppBarStateChangeListener: AppBarLayout.OnOffsetChangedListener {

    private var mCurrentState = AppBarState.IDLE

    override fun onOffsetChanged(appBarLayout: AppBarLayout, i: Int) {
        mCurrentState = when {
            i == 0 -> {
                if (mCurrentState !== AppBarState.EXPANDED) {
                    onStateChanged(appBarLayout, AppBarState.EXPANDED)
                }
                AppBarState.EXPANDED
            }
            abs(i) >= appBarLayout.totalScrollRange -> {
                if (mCurrentState !== AppBarState.COLLAPSED) {
                    onStateChanged(appBarLayout, AppBarState.COLLAPSED)
                }
                AppBarState.COLLAPSED
            }
            else -> {
                if (mCurrentState !== AppBarState.IDLE) {
                    onStateChanged(appBarLayout, AppBarState.IDLE)
                }
                AppBarState.IDLE
            }
        }
    }

    public abstract fun onStateChanged(appBarLayout: AppBarLayout, state: AppBarState)
}