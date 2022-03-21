package network.etna.etnawallet.ui.walletplatform.tokenlist

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.android.schedulers.AndroidSchedulers
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentWalletTokenListBinding
import network.etna.etnawallet.databinding.ViewholderTokenListItemBinding
import network.etna.etnawallet.repository.warningservice.subscribeWithErrorHandler
import network.etna.etnawallet.sdk.api.ImageProvider
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.helpers.extensions.dpToPx
import network.etna.etnawallet.ui.helpers.recycler.RxRecyclerAdapter
import network.etna.etnawallet.ui.helpers.recycler.addViewHolderSpacing
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalletPlatformTokenListFragment :
    BaseFragment<FragmentWalletTokenListBinding>(
        FragmentWalletTokenListBinding::inflate,
        R.string.wallet_platform_tokens_list
    ) {

    private val viewModel: WalletPlatformTokenListViewModel by viewModel()

    private val imageSize: Int by lazy {
        requireContext().dpToPx(24)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel
            .getPlatformObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWithErrorHandler { model ->
                Glide
                    .with(requireContext())
                    .load(ImageProvider.getPlatformImageURL(model.id, imageSize))
                    .into(binding.platformImageView)
                binding.platformName.text = resources.getString(R.string.wallet_platform_settings_platform_name, model.name, model.symbol)
            }
            .disposedWith(viewLifecycleOwner)

        val adapter = RxRecyclerAdapter(
            viewLifecycleOwner,
            R.layout.viewholder_token_list_item,
            ViewholderTokenListItemBinding::bind,
            viewModel.getTokenListObservable(),
            { bindModel ->
                val binding = bindModel.binding
                val model = bindModel.model
                binding.titleTextView.text = resources.getString(R.string.fragment_search_token_tokeninfo_title, model.name, model.symbol)

                binding.eyeImageView.isActivated = model.isVisible

                Glide
                    .with(requireContext())
                    .load(ImageProvider.getTokenURL(model.tokenAddress, viewModel.getPlatformId(), imageSize))
                    .into(binding.tokenImageView)
            },
            { selectModel ->
                val model = selectModel.model
                viewModel.updateTokenVisibility(model.tokenAddress, !model.isVisible)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = adapter
        context?.let {
            binding.recyclerView.addViewHolderSpacing(16, it)
        }
    }
}