package network.etna.etnawallet.ui.helpers.extensions

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import network.etna.etnawallet.R

fun Fragment.dpToPx(dp: Int): Int {
    return requireContext()
        .dpToPx(dp)
}

fun Fragment.navigate(fragmentId: Int, args: Bundle?, optionsBuilder: (NavOptionsBuilder.() -> Unit)? = null) {
    findNavController().navigate(
        fragmentId,
        args,
        navOptions {
            anim {
                enter = R.anim.slide_in_right
                exit = R.anim.fade_out
                popEnter = R.anim.slide_in_left
                popExit = R.anim.fade_out
            }
            optionsBuilder?.let {
                apply(it)
            }
        }
    )
}