package network.etna.etnawallet.ui.asset.send.selecttoken

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentSelectTokenBinding
import network.etna.etnawallet.databinding.ViewholderSelectTokenBinding
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.helpers.extensions.dpToPx
import network.etna.etnawallet.ui.helpers.recycler.RxRecyclerAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class SelectTokenFragment :
    BaseFragment<FragmentSelectTokenBinding>(
        FragmentSelectTokenBinding::inflate,
        R.string.select_token_title
    ) {

    private val viewModel: SelectTokenViewModel by viewModel()

    private val tokenImageSize: Int by lazy {
        requireContext().dpToPx(24)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RxRecyclerAdapter(
            viewLifecycleOwner,
            R.layout.viewholder_select_token,
            ViewholderSelectTokenBinding::bind,
            viewModel.getTokensObservable(),
            { bindModel ->
                val binding = bindModel.binding
                val model = bindModel.model

                binding.textView.text = model.name

                binding.radio.isActivated = model.isSelected

                context?.let { context ->
                    Glide
                        .with(context)
                        .load(model.iconURLProvider(tokenImageSize))
                        .into(binding.tokenImageView)
                }
            },
            { selectModel ->
                val model = selectModel.model
                viewModel.selectAssetIdHolder(model.assetIdHolder)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = adapter
//        context?.let {
//            binding.recyclerView.addViewHolderSpacing(16, it)
//        }
    }
}