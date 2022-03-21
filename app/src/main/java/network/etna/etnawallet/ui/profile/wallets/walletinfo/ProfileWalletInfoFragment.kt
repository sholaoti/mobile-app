package network.etna.etnawallet.ui.profile.wallets.walletinfo

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionManager
import network.etna.etnawallet.R
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.databinding.FragmentProfileWalletInfoBinding
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import network.etna.etnawallet.ui.helpers.extensions.toggleVisibilityGone
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ProfileWalletInfoFragment :
    BaseFragment<FragmentProfileWalletInfoBinding>(
        FragmentProfileWalletInfoBinding::inflate,
        null,
        R.menu.fragment_profile_wallet_info_menu
    ) {

    private val args: ProfileWalletInfoFragmentArgs by navArgs()
    private val viewModel: ProfileWalletInfoViewModel by viewModel { parametersOf(args.walletId) }
    private var menu: Menu? = null

    override fun getTitleObservable(): Observable<String> {
        return viewModel
            .getWalletNameObservable()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel
            .isWalletActiveObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                try {
                    view.findViewById<Toolbar>(R.id.includeToolbar)
                        .menu
                        ?.findItem(R.id.action_delete)
                        ?.isVisible = !it
                } catch (e: Exception) {}

                val transition: Transition = Fade()
                transition.duration = 500
                transition.addTarget(R.id.activeWalletTextView)
                transition.addTarget(R.id.setAsActiveWalletTextView)

                TransitionManager.beginDelayedTransition(binding.root, transition)

                binding.activeWalletTextView.toggleVisibilityGone(it)
                binding.setAsActiveWalletTextView.toggleVisibilityGone(!it)
            }.disposedWith(viewLifecycleOwner)

        binding.setAsActiveWalletTextView
            .setOnClickListener {
                viewModel.makeActiveWallet()
            }

        binding.changeNameTextView.setOnClickListener {
            findNavController().navigate(ProfileWalletInfoFragmentDirections.toChangeWalletNameFragment(args.walletId))
        }

        binding.getRecoveryPhraseTextView.toggleVisibilityGone(viewModel.hasRecoveryPhrase())

        binding.getRecoveryPhraseTextView.setOnClickListener {
            findNavController().navigate(ProfileWalletInfoFragmentDirections.toRecoveryPhraseActivity(args.walletId, true))
        }
    }

    override fun onResume() {
        super.onResume()
        if (!viewModel.isWalletAvailable()) {
            findNavController().popBackStack()
        }
    }

    override fun menuItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                findNavController().navigate(ProfileWalletInfoFragmentDirections.toDeleteWalletActivity(args.walletId))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}