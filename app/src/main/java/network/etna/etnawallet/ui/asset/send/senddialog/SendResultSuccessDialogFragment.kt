package network.etna.etnawallet.ui.asset.send.senddialog

import android.os.Bundle
import android.view.View
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentSendResultSuccessDialogBinding
import network.etna.etnawallet.ui.base.BaseModalDialogFragment

class SendResultSuccessDialogFragment: BaseModalDialogFragment<FragmentSendResultSuccessDialogBinding>(
    FragmentSendResultSuccessDialogBinding::inflate,
    R.string.send_result_success_dialog_title
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.doneButton.setOnClickListener {
            closeAction()
        }
    }

    override fun closeAction() {
        activity?.finish()
    }

}