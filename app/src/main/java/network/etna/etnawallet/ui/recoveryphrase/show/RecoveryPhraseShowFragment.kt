package network.etna.etnawallet.ui.recoveryphrase.show

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import io.reactivex.Observable
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentRecoveryPhraseShowBinding
import network.etna.etnawallet.databinding.RecyclerWordItemBinding
import network.etna.etnawallet.ui.addwallet.importmultiwallet.WordModel
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.helpers.RecyclerViewMargin
import network.etna.etnawallet.ui.helpers.extensions.screenWidth
import network.etna.etnawallet.ui.helpers.extensions.toPx
import network.etna.etnawallet.ui.helpers.recycler.RxRecyclerAdapter
import network.etna.etnawallet.ui.helpers.recycler.RxViewHolder
import org.koin.androidx.viewmodel.ext.android.viewModel

class RecoveryPhraseShowFragment :
    BaseFragment<FragmentRecoveryPhraseShowBinding>(
        FragmentRecoveryPhraseShowBinding::inflate,
        R.string.recovery_phrase_title,
        R.menu.fragment_info_menu
    ) {

    private val viewModel: RecoveryPhraseShowViewModel by viewModel()

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

        binding.continueButton.setOnClickListener {
            findNavController().navigate(RecoveryPhraseShowFragmentDirections.toRecoveryPhraseVerifyFragment())
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

    private fun getAdapter(dataObservable: Observable<List<WordModel>>, positionOffset: Int): RecyclerView.Adapter<RxViewHolder<RecyclerWordItemBinding>> {
        return RxRecyclerAdapter(
            viewLifecycleOwner,
            R.layout.recycler_word_item,
            RecyclerWordItemBinding::bind,
            dataObservable,
            { bindModel ->
                val binding = bindModel.binding
                val model = bindModel.model
                binding.itemText.text = model.word
            }
        )
    }

    override fun menuItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_info -> {
                findNavController().navigate(RecoveryPhraseShowFragmentDirections.toRecoveryPhraseInfoFragment())
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }
}