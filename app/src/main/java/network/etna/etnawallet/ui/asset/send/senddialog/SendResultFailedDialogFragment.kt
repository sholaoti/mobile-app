package network.etna.etnawallet.ui.asset.send.senddialog

import android.os.Bundle
import android.view.View
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentSendResultFailedDialogBinding
import network.etna.etnawallet.databinding.FragmentSendResultSuccessDialogBinding
import network.etna.etnawallet.ui.base.BaseModalDialogFragment

class SendResultFailedDialogFragment: BaseModalDialogFragment<FragmentSendResultFailedDialogBinding>(
    FragmentSendResultFailedDialogBinding::inflate,
    R.string.send_result_failed_dialog_title
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.doneButton.setOnClickListener {
            closeAction()
        }
    }
    
}