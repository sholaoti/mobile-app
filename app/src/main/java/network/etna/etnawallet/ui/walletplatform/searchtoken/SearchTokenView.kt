package network.etna.etnawallet.ui.walletplatform.searchtoken

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.LayoutSearchTokenBinding
import network.etna.etnawallet.repository.wallets.etnawallet.URLProvider
import network.etna.etnawallet.ui.helpers.extensions.bubbleAnimation
import network.etna.etnawallet.ui.helpers.extensions.dpToPx
import network.etna.etnawallet.ui.helpers.extensions.toggleVisibility

sealed class SearchTokenViewState {
    object Searching: SearchTokenViewState()
    object Failed: SearchTokenViewState()
    class Found(val tokenImageProvider: URLProvider?, val title: String, val subtitle: String): SearchTokenViewState()
}

class SearchTokenView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private var _binding: LayoutSearchTokenBinding? = null
    val binding get() = _binding!!

    var state: SearchTokenViewState = SearchTokenViewState.Searching
        private set

    private val imageSize: Int by lazy {
        context.dpToPx(64)
    }

    init {
        _binding = LayoutSearchTokenBinding.inflate(LayoutInflater.from(context), this, true)
        clipChildren = false
        binding.searchBackground.bubbleAnimation()
        binding.searchIcon.bubbleAnimation()
    }

    fun updateState(newState: SearchTokenViewState) {
        if (newState == state) {
            return
        }

        state = newState

        val transition: Transition = Fade()
        transition.duration = 300
        transition.addTarget(R.id.searchBackground)
        transition.addTarget(R.id.searchIcon)
        transition.addTarget(R.id.failedBackground)
        transition.addTarget(R.id.failedIcon)
        transition.addTarget(R.id.foundBackground)
        transition.addTarget(R.id.foundIcon)

        TransitionManager.beginDelayedTransition(binding.root, transition)

        when (newState) {
            SearchTokenViewState.Searching -> binding.searchIcon.bringToFront()
            SearchTokenViewState.Failed -> binding.failedIcon.bringToFront()
            is SearchTokenViewState.Found -> {
                binding.foundIcon.bringToFront()
                context?.let {

                    newState.tokenImageProvider?.invoke(imageSize)?.apply {
                        Glide
                            .with(it)
                            .load(this)
                            .into(binding.foundIcon)
                    } ?: run {
                        binding.foundIcon.setImageBitmap(null)
                    }

                }
            }
        }

        binding.searchBackground.toggleVisibility(state == SearchTokenViewState.Searching)
        binding.searchIcon.toggleVisibility(state == SearchTokenViewState.Searching)
        binding.failedBackground.toggleVisibility(state == SearchTokenViewState.Failed)
        binding.failedIcon.toggleVisibility(state == SearchTokenViewState.Failed)
        binding.foundBackground.toggleVisibility(state is SearchTokenViewState.Found)
        binding.foundIcon.toggleVisibility(state is SearchTokenViewState.Found)

    }
}