package network.etna.etnawallet.ui.addwallet.addwallet

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentAddWalletBinding
import network.etna.etnawallet.ui.base.BaseFragment

class AddWalletFragment :
    BaseFragment<FragmentAddWalletBinding>(
        FragmentAddWalletBinding::inflate,
        R.string.add_wallet
    ) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.openWalletButton.setOnClickListener {
            findNavController().navigate(AddWalletFragmentDirections.toImportWalletFragment())
        }

        binding.addWalletButton.setOnClickListener {
            findNavController().navigate(AddWalletFragmentDirections.toCreateMultiWalletFragment())
        }
    }
}