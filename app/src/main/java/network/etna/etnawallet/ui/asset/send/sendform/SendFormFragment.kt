package network.etna.etnawallet.ui.asset.send.sendform

import android.Manifest
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentSendFormBinding
import network.etna.etnawallet.repository.warningservice.WarningService
import network.etna.etnawallet.sdk.api.ImageProvider
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.helpers.extensions.afterTextChanged
import network.etna.etnawallet.ui.helpers.extensions.dpToPx
import network.etna.etnawallet.ui.helpers.extensions.formatAmount
import network.etna.etnawallet.ui.helpers.extensions.toggleVisibilityGone
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SendFormFragment :
    BaseFragment<FragmentSendFormBinding>(
        FragmentSendFormBinding::inflate,
        R.string.send_form_title
    ) {
    private val viewModel: SendFormViewModel by viewModel()
    private val warningService: WarningService by inject()

    private val tokenImageSize: Int by lazy {
        requireContext().dpToPx(24)
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                findNavController().navigate(SendFormFragmentDirections.toQRScannerDialogFragment())
            } else {
                warningService.handleIssue(NoCameraPermissionError.NoCamera)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel
            .getAssetObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { asset ->
                    resources
                        .getString(R.string.send_form_available_funds, asset.balance?.formatAmount() ?: "-")
                        .apply {
                            //binding.availableTextView.text = this
                        }//1300973301

//                    context?.let { context ->
//                        Glide
//                            .with(context)
//                            .load(ImageProvider.getImageURL(asset.referenceId, tokenImageSize))
//                            .into(binding.tokenImageView)
//                    }

                    //binding.tokenNameTextView.text = asset.name

                    resources
                        .getString(R.string.send_form_button, asset.name)
                        .apply {
                            binding.sendButton.binding.textView.text = this
                        }
                },
                {
                    val x = 1
                }
            )
            .disposedWith(viewLifecycleOwner)

        viewModel
            .getTokenCurrencyNameObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    binding.tokenNameTextView.text = it
                },
                {}
            )
            .disposedWith(viewLifecycleOwner)

        binding.qrCodeButton.setOnClickListener {
            startQRScanner()
        }

        binding.tokenNameTextView.setOnClickListener {
            binding.amountTextField.setText(viewModel.changeCurrencyPreference())
            binding.amountTextField.setSelection(binding.amountTextField.text.length)
        }

//        binding.tokenSelectorContainer.setOnClickListener {
//            findNavController().navigate(SendFormFragmentDirections.toSelectTokenFragment())
//        }

        viewModel
            .getRecipientAddressObservable()
            .filter {
                it.isPresent && binding.recipientTextField.text.toString() != it.get()
            }
            .map { it.get() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    binding.recipientTextField.setText(it)
                },
                {

                }
            )
            .disposedWith(viewLifecycleOwner)

        binding.amountTextField.afterTextChanged { viewModel.amountTextChanged(it) }

        binding.recipientTextField.afterTextChanged { viewModel.recipientTextChanged(it) }

        binding.sendButton.isEnabled = false

        binding
            .sendButton
            .bind(
                {
                    viewModel.sendPressed()
                },
                viewLifecycleOwner,
                {
                    findNavController().navigate(SendFormFragmentDirections.toSendResultSuccessDialogFragment())
                },
                {
                    findNavController().navigate(SendFormFragmentDirections.toSendResultFailedDialogFragment())
                },
                false
            )

        viewModel
            .getSendAvailableObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.sendButton.isEnabled = it
            }
            .disposedWith(viewLifecycleOwner)

        viewModel
            .getAmountValidObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.amountContainer.isActivated = !it
            }
            .disposedWith(viewLifecycleOwner)

        viewModel
            .getRecipientValidObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.recipientContainer.isActivated = !it
            }
            .disposedWith(viewLifecycleOwner)

        binding.maxTextView.setOnClickListener {
            viewModel
                .allFundsPressed()
                .subscribe(
                    {
                        binding.amountTextField.setText(it)
                    },
                    {}
                )
                .disposedWith(viewLifecycleOwner)
        }

        binding.pasteTextView.setOnClickListener {

            try {
                val manager = requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val text = manager.primaryClip!!.getItemAt(0).text.toString()
                viewModel.recipientTextChanged(text)
            } catch (e: Exception) {}

        }

        viewModel
            .getAdditionalAmountObservable()
            .subscribe(
                {
                    if (it.isEmpty()) {
                        binding.availableTextView.text = ""
                    } else {
                        binding.availableTextView.text = "≈ $it"
                    }
                },
                {}
            )
            .disposedWith(viewLifecycleOwner)

        Observable.combineLatest(
            viewModel.isRateAvailableObservable(),
            viewModel.isPreferCurrencyObservable()
            ) { isRateAvailable, isPreferCurrency ->
                Pair(isRateAvailable, isPreferCurrency)
            }
            .subscribe(
                {
                    val isRateAvailable = it.first
                    val isPreferCurrency = it.second

                    binding.tokenNameTextView.toggleVisibilityGone(isRateAvailable)
                    binding.maxTextView.toggleVisibilityGone(!isPreferCurrency)
                    binding.tokenSeparator.toggleVisibilityGone(isRateAvailable && !isPreferCurrency)
                },
                {}
            )
            .disposedWith(viewLifecycleOwner)

    }

    private fun startQRScanner() {
        activityResultLauncher.launch(Manifest.permission.CAMERA)
    }
}