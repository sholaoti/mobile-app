package network.etna.etnawallet.ui.recoveryphrase.initial

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.findNavController
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.android.schedulers.AndroidSchedulers
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentRecoveryPhraseInitialBinding
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.recoveryphrase.RecoveryPhraseScenarioModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class RecoveryPhraseInitialFragment :
    BaseFragment<FragmentRecoveryPhraseInitialBinding>(
        FragmentRecoveryPhraseInitialBinding::inflate,
        R.string.recovery_phrase_title,
        R.menu.fragment_info_menu
    ) {

    private val viewModel: RecoveryPhraseInitialViewModel by viewModel()
    private val scenarioModel: RecoveryPhraseScenarioModel by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getRecoveryPhraseDescAcceptedObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.continueButton.isEnabled = it
                binding.recoveryPhraseLostDescTextView.isActivated = it
            }
            .disposedWith(viewLifecycleOwner)

        binding.recoveryPhraseLostDescTextView
            .setOnClickListener {
                viewModel.acceptRecoveryPhraseDesc()
            }

        binding.laterButton
            .setOnClickListener {
                requireActivity().finish()
            }

        binding.continueButton
            .setOnClickListener {
                if (scenarioModel.checkOTP) {
                    findNavController().navigate(RecoveryPhraseInitialFragmentDirections.toOtpFragment())
                } else {
                    findNavController().navigate(RecoveryPhraseInitialFragmentDirections.toRecoveryPhraseShowFragment())
                }
            }
    }

    override fun menuItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_info -> {
                findNavController().navigate(RecoveryPhraseInitialFragmentDirections.toRecoveryPhraseInfoFragment())
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }
}