package network.etna.etnawallet.ui.recoveryphrase.success

import android.os.Bundle
import android.view.View
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentRecoveryPhraseSuccessBinding
import network.etna.etnawallet.ui.base.BaseFragment

class RecoveryPhraseSuccessFragment :
    BaseFragment<FragmentRecoveryPhraseSuccessBinding>(
        FragmentRecoveryPhraseSuccessBinding::inflate,
        R.string.recovery_phrase_success_title
    ) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.continueButton.setOnClickListener {
            requireActivity().finish()
        }
    }

}