package network.etna.etnawallet.ui.addwallet.importmultiwallet

import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentImportMultiWalletBinding
import network.etna.etnawallet.databinding.RecyclerWordItemDeletableBinding
import network.etna.etnawallet.repository.warningservice.WarningService
import network.etna.etnawallet.repository.warningservice.subscribeWithErrorHandler
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.helpers.RecyclerViewMargin
import network.etna.etnawallet.ui.helpers.extensions.afterTextChanged
import network.etna.etnawallet.ui.helpers.extensions.screenWidth
import network.etna.etnawallet.ui.helpers.extensions.toPx
import network.etna.etnawallet.ui.helpers.recycler.RxRecyclerAdapter
import network.etna.etnawallet.ui.helpers.recycler.RxViewHolder
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ImportMultiWalletFragment :
    BaseFragment<FragmentImportMultiWalletBinding>(
        FragmentImportMultiWalletBinding::inflate,
        R.string.import_multi_wallet
    ) {

    private val viewModel: ImportMultiWalletViewModel by viewModel()

    private val warningService: WarningService by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel
            .getImportAvailableObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.importButton.isEnabled = it
            }.disposedWith(viewLifecycleOwner)

        binding.walletNameTextField.afterTextChanged { viewModel.walletNameChanged(it) }

        binding.recoveryPhraseTextField.afterTextChanged {
            if (it.contains(" ")) {
                val shouldJumpToEnd = it.count { it == ' ' } > 1
                var selection = it.indexOf(" ")
                val text = it.replace(" ", "")
                if (shouldJumpToEnd) {
                    selection = text.length
                }
                binding.recoveryPhraseTextField.setText(text)
                binding.recoveryPhraseTextField.setSelection(selection)

                warningService.handleIssue(SeedPhraseInputError)
            }
        }
        binding.recoveryPhraseTextField.setOnEditorActionListener { textView, actionId, keyEvent ->

            if (actionId == 6) {
                val text = textView.text.toString()
                textView.text = null

                when (viewModel.addWord(text)) {
                    AddWordStatus.FINISHED -> return@setOnEditorActionListener false
                    AddWordStatus.SUCCESS -> return@setOnEditorActionListener true
                    AddWordStatus.FAILED -> return@setOnEditorActionListener true
                }
            }
            return@setOnEditorActionListener false
        }

        viewModel
            .isEyeActivatedObservable()
            .subscribe {
                if (it) {
                    binding.recoveryPhraseTextField.inputType = InputType.TYPE_CLASS_TEXT
                    binding.eyeImageView.isActivated = true
                } else {
                    binding.recoveryPhraseTextField.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    binding.eyeImageView.isActivated = false
                }
                binding.recoveryPhraseTextField.typeface = binding.walletNameTextField.typeface

                binding.recoveryPhraseTextField.setSelection(binding.recoveryPhraseTextField.text.length)
            }
            .disposedWith(viewLifecycleOwner)

        binding.eyeImageView.setOnClickListener {
            viewModel.invertEye()
        }

        binding.importButton.setOnClickListener {
            binding.importButton.isEnabled = false
            viewModel.importPressed().subscribeWithErrorHandler(
                {
                    activity?.finish()
                },
                {
                    binding.importButton.isEnabled = true
                }
            ).disposedWith(viewLifecycleOwner)
        }

        val decoration = RecyclerViewMargin(10.toPx.toInt(), 4)
        val scale = (activity?.screenWidth() ?: 0) / 450.toPx

        listOf(binding.recyclerView1, binding.recyclerView2, binding.recyclerView3).forEachIndexed { index, recyclerView ->
            recyclerView.layoutManager = getLayoutManager()
            recyclerView.addItemDecoration(decoration)
            val positionOffset = 4 * index
            recyclerView.adapter = getAdapter(viewModel.getDataObservable().map { it.drop(positionOffset).take(4) }, positionOffset)
            recyclerView.scaleX = scale
            recyclerView.scaleY = scale
        }
    }

    private fun getLayoutManager(): RecyclerView.LayoutManager {
        val lm = FlexboxLayoutManager(context)
        lm.flexDirection = FlexDirection.ROW
        lm.justifyContent = JustifyContent.CENTER
        lm.alignItems = AlignItems.STRETCH
        lm.maxLine = 1
        return lm
    }

    private fun getAdapter(dataObservable: Observable<List<WordModel>>, positionOffset: Int): RecyclerView.Adapter<RxViewHolder<RecyclerWordItemDeletableBinding>> {
        return RxRecyclerAdapter(
            viewLifecycleOwner,
            R.layout.recycler_word_item_deletable,
            RecyclerWordItemDeletableBinding::bind,
            dataObservable,
            { bindModel ->
                val binding = bindModel.binding
                val model = bindModel.model

                binding.itemText.text = model.word
                //binding.itemText.measure(0, 0)
            },
            { selectModel ->
                viewModel.removeWord(selectModel.position + positionOffset)
            }
        )
    }
}