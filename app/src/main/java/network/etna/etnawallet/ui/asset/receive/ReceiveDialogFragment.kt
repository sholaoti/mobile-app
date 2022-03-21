package network.etna.etnawallet.ui.asset.receive

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.ViewTreeObserver
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentReceiveBinding
import network.etna.etnawallet.ui.base.BaseModalDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.content.Intent

class ReceiveDialogFragment: BaseModalDialogFragment<FragmentReceiveBinding>(
    FragmentReceiveBinding::inflate,
    R.string.receive_dialog_title
) {

    private val viewModel: ReceiveViewModel by viewModel()

    private val layoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val width = binding.qrImageView.width
        if (width > 0) {
            binding.qrImageView.setImageBitmap(viewModel.getQrCodeBitmap(width))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.qrTextView.text = viewModel.getWalletAddress()

        binding.qrImageView.setOnLongClickListener {
            copyToClipboard()
            true
        }

        binding.qrTextView.setOnLongClickListener {
            copyToClipboard()
            true
        }

        binding.copyButton.setOnClickListener {
            copyToClipboard()
        }

        binding.shareButton.setOnClickListener {
            share()
        }
    }

    private var copiedAnimator: ViewPropertyAnimator? = null

    private fun copyToClipboard() {

        copiedAnimator?.cancel()

        binding.qrImageView.alpha = 0.1F
        binding.copiedTextView.alpha = 1F

        binding.qrImageView
            .animate()
            .apply {
                duration = 1000
            }
            .alpha(1F)
            .setListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    copiedAnimator = safeBinding
                        ?.copiedTextView
                        ?.animate()
                        ?.alpha(0F)
                }
            })

        activity?.apply {
            (getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).apply {
                setPrimaryClip(ClipData.newPlainText(viewModel.getWalletAddress(), viewModel.getWalletAddress()))
            }

        }
    }

    private fun share() {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        val shareBody = viewModel.getWalletAddress()
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, viewModel.getWalletAddress())
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        startActivity(Intent.createChooser(sharingIntent, ""))
    }

    override fun onResume() {
        super.onResume()
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
        layoutListener.onGlobalLayout()
    }

    override fun onPause() {
        super.onPause()
        binding.root.viewTreeObserver.removeOnGlobalLayoutListener(layoutListener)
    }


}