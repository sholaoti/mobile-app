package network.etna.etnawallet.ui.recoveryphrase.verify

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.Observable
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentRecoveryPhraseVerifyBinding
import network.etna.etnawallet.databinding.RecyclerWordItemBinding
import network.etna.etnawallet.ui.addwallet.importmultiwallet.WordStatus
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.helpers.RecyclerViewMargin
import network.etna.etnawallet.ui.helpers.extensions.*
import network.etna.etnawallet.ui.helpers.recycler.RxRecyclerAdapter
import network.etna.etnawallet.ui.helpers.recycler.RxViewHolder
import org.koin.androidx.viewmodel.ext.android.viewModel

class RecoveryPhraseVerifyFragment :
    BaseFragment<FragmentRecoveryPhraseVerifyBinding>(
        FragmentRecoveryPhraseVerifyBinding::inflate,
        R.string.recovery_phrase_verify_title,
        R.menu.fragment_info_menu
    ) {

    private val viewModel: RecoveryPhraseVerifyViewModel by viewModel()

    override val isSecure: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        binding.tryAgainTextView.setOnClickListener {
            viewModel.tryAgain()
        }

        binding.tryAgainTextView.translationY = dpToPx(100).toFloat()

        viewModel.getRecoveryPhraseVerifyStateObservable()
            .subscribe { state ->
                when (state) {
                    RecoveryPhraseVerifyState.NORMAL ->
                        binding.tryAgainTextView.animate().translationY(dpToPx(100).toFloat())
                    RecoveryPhraseVerifyState.FAILED ->
                        binding.tryAgainTextView.animate().translationY(0F)
                    RecoveryPhraseVerifyState.SUCCESS ->
                        findNavController().navigate(RecoveryPhraseVerifyFragmentDirections.toRecoveryPhraseSuccessFragment())
                }
            }
            .disposedWith(viewLifecycleOwner)
    }

    private fun getLayoutManager(): RecyclerView.LayoutManager {
        val lm = FlexboxLayoutManager(context)
        lm.flexDirection = FlexDirection.ROW
        lm.justifyContent = JustifyContent.CENTER
        lm.alignItems = AlignItems.STRETCH
        lm.maxLine = 1
        return lm
    }

    private fun getAdapter(dataObservable: Observable<List<Int>>, positionOffset: Int): RecyclerView.Adapter<RxViewHolder<RecyclerWordItemBinding>> {
        return RxRecyclerAdapter(
            viewLifecycleOwner,
            R.layout.recycler_word_item,
            RecyclerWordItemBinding::bind,
            dataObservable,
            { bindModel ->
                val viewBinding = bindModel.binding

                viewModel
                    .getWordStatusObservable(bindModel.position + positionOffset)
                    .subscribe {
                        viewBinding.itemText.text = it.word
                        viewBinding.itemTextFailed.text = it.word

                        val transition: Transition = Fade()
                        transition.duration = 300
                        transition.addTarget(R.id.item_text)
                        transition.addTarget(R.id.item_text_failed)
                        transition.addTarget(R.id.item_text_success)

                        TransitionManager.beginDelayedTransition(binding.root, transition)

                        viewBinding.itemText.toggleVisibility(it.status == WordStatus.NORMAL)
                        viewBinding.itemTextSuccess.toggleVisibility(it.status == WordStatus.SUCCESS)
                        viewBinding.itemTextFailed.toggleVisibility(it.status == WordStatus.ERROR)

                        if (it.status == WordStatus.ERROR) {
                            viewBinding.root.shake(25F)
                        }

                    }
                    .disposedWith(viewLifecycleOwner)
            },
            {
                viewModel.selectWord(it.position + positionOffset)
            }
        )
    }

    override fun menuItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_info -> {
                findNavController().navigate(RecoveryPhraseVerifyFragmentDirections.toRecoveryPhraseInfoFragment())
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }
}