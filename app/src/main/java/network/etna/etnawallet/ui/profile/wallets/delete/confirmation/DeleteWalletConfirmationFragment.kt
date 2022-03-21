package network.etna.etnawallet.ui.profile.wallets.delete.confirmation

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.android.schedulers.AndroidSchedulers
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentProfileWalletDeleteConfirmationBinding
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.helpers.extensions.afterTextChanged
import org.koin.androidx.viewmodel.ext.android.viewModel

class DeleteWalletConfirmationFragment :
    BaseFragment<FragmentProfileWalletDeleteConfirmationBinding>(
        FragmentProfileWalletDeleteConfirmationBinding::inflate,
        R.string.delete_wallet
    ) {

    private val viewModel: DeleteWalletConfirmationViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isDeleteDescAccepted()
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.deleteWalletDescTextView.isActivated = it
            }
            .disposedWith(viewLifecycleOwner)

        viewModel.isDeleteWalletAllowed()
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.deleteButton.isEnabled = it
            }
            .disposedWith(viewLifecycleOwner)

        binding.deleteWalletDescTextView
            .setOnClickListener {
                viewModel.acceptDeleteDesc()
            }

        binding.confirmTextField.afterTextChanged { viewModel.phraseChanged(it) }

        binding.deleteButton.bind(
            {
                viewModel.deleteWallet()
            },
            viewLifecycleOwner,
            {
                findNavController().navigate(DeleteWalletConfirmationFragmentDirections.toDeleteWalletSuccessFragment())
            }
        )
    }
}
