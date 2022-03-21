package network.etna.etnawallet.ui.walletplatform.searchtoken

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentSearchTokenBinding
import network.etna.etnawallet.repository.wallets.etnawallet.fromJsonString
import network.etna.etnawallet.ui.base.BaseModalDialogFragment
import network.etna.etnawallet.ui.helpers.extensions.animateDots
import network.etna.etnawallet.ui.helpers.extensions.toggleVisibility
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class WalletPlatformSearchTokenFragment : BaseModalDialogFragment<FragmentSearchTokenBinding>(
    FragmentSearchTokenBinding::inflate,
    R.string.fragment_search_token_title
) {

    private val args: WalletPlatformSearchTokenFragmentArgs by navArgs()
    private val viewModel: WalletPlatformSearchTokenViewModelInterface by viewModel { parametersOf(args.input.fromJsonString(WalletPlatformSearchTokenViewModelInput::class.java)) }

    var state: SearchTokenViewState = SearchTokenViewState.Searching
        private set

    private val titleResourceSubject: BehaviorSubject<Int> = BehaviorSubject.createDefault(R.string.fragment_search_token_title)
    
    override fun getTitleObservable(): Observable<String> {
        return titleResourceSubject
            .map {
                context?.getString(it) ?: ""
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.confirmButton.isEnabled = false

        binding.confirmButton.setOnClickListener {
            viewModel.addToken()
            activity?.finish()
        }

        binding.tryAgainButton.setOnClickListener {
            updateState(SearchTokenViewState.Searching)
        }

        binding.searchingSubtitleTextView.animateDots().disposedWith(viewLifecycleOwner)

        viewModel
            .getStateObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                updateState(it)
            }
            .disposedWith(viewLifecycleOwner)
    }

    private fun updateState(newState: SearchTokenViewState) {
        if (newState == state) {
            return
        }

        state = newState

        binding.searchTokenView.updateState(state)

        val transition: Transition = Fade()
        transition.duration = 300
        transition.addTarget(R.id.searchingSubtitleTextView)
        transition.addTarget(R.id.failedSubtitleTextView)
        transition.addTarget(R.id.confirmButton)
        transition.addTarget(R.id.tryAgainButton)
        transition.addTarget(R.id.tokenTitleTextView)
        transition.addTarget(R.id.tokenSubtitleTextView)

        TransitionManager.beginDelayedTransition(binding.root, transition)

        binding.searchingSubtitleTextView.toggleVisibility(state == SearchTokenViewState.Searching)
        binding.confirmButton.toggleVisibility(state == SearchTokenViewState.Searching || state is SearchTokenViewState.Found)
        binding.failedSubtitleTextView.toggleVisibility(state == SearchTokenViewState.Failed)
        binding.tryAgainButton.toggleVisibility(state == SearchTokenViewState.Failed)

        binding.tokenTitleTextView.toggleVisibility(state is SearchTokenViewState.Found)
        binding.tokenSubtitleTextView.toggleVisibility(state is SearchTokenViewState.Found)

        when (newState) {
            SearchTokenViewState.Searching -> {
                binding.confirmButton.isEnabled = false
                titleResourceSubject.onNext(R.string.fragment_search_token_title)
            }
            SearchTokenViewState.Failed -> {
                binding.confirmButton.isEnabled = false
                titleResourceSubject.onNext(R.string.fragment_search_token_title_failed)
            }
            is SearchTokenViewState.Found -> {
                binding.confirmButton.isEnabled = true
                titleResourceSubject.onNext(R.string.fragment_search_token_title)
                binding.tokenTitleTextView.text = newState.title
                binding.tokenSubtitleTextView.text = newState.subtitle
            }
        }
    }
}
