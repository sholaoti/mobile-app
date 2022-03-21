package network.etna.etnawallet.ui.addwallet.createmultiwallet

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.navigation.fragment.findNavController
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.android.schedulers.AndroidSchedulers
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentCreateMultiWalletBinding
import network.etna.etnawallet.ui.addwallet.ImportWalletScenarioModel
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.base.buttons.MainButtonState
import network.etna.etnawallet.ui.helpers.extensions.afterTextChanged
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class CreateMultiWalletFragment :
    BaseFragment<FragmentCreateMultiWalletBinding>(
        FragmentCreateMultiWalletBinding::inflate,
        R.string.create_wallet
    ) {

    private val scenarioModel: ImportWalletScenarioModel by inject()
    private val viewModel: CreateMultiWalletViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel
            .getCreateAvailableObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.addWalletButton.isEnabled = it
            }.disposedWith(viewLifecycleOwner)

        binding.walletNameTextField.afterTextChanged { viewModel.walletNameChanged(it) }

        binding.addWalletButton.setOnClickListener {
            if (binding.addWalletButton.state == MainButtonState.NORMAL) {
                binding.addWalletButton.state = MainButtonState.LOADING

                viewModel
                    .createMultiWallet()
                    .delay(2, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { walletId ->
                            binding.addWalletButton.state = MainButtonState.SUCCESS
                            Handler(Looper.getMainLooper()).postDelayed({
                                scenarioModel.overrideActivityFinishAnimation = false
                                scenarioModel.navigateToMain = false
                                findNavController().navigate(CreateMultiWalletFragmentDirections.toRecoveryPhraseActivity(walletId, false))
                                requireActivity().finish()

                            }, 1200)
                        },
                        {
                            binding.addWalletButton.state = MainButtonState.NORMAL
                        }
                    )
                    .disposedWith(viewLifecycleOwner)
            }
        }
    }
}