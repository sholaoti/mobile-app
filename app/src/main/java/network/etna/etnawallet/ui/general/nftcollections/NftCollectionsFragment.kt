package network.etna.etnawallet.ui.general.nftcollections

import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentNftCollectionsBinding
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.helpers.extensions.formatAmount
import network.etna.etnawallet.ui.helpers.recycler.RxRecyclerAdapter

class NftCollectionsFragment :
    BaseFragment<FragmentNftCollectionsBinding>(
        FragmentNftCollectionsBinding::inflate,
    R.string.nft_collections_title
) {
//    val adapter = RxRecyclerAdapter(
//        viewLifecycleOwner,
//        R.layout.viewholder_nft_collection,
//        ViewholderSelectWalletBinding::bind,
//        viewModel.getWalletModelsObservable(),
//        { bindModel ->
//            val binding = bindModel.binding
//            val model = bindModel.model
//
//        },
//        { selectModel ->
//            val model = selectModel.model
//            viewModel.selectActiveWallet(model.id)
//        }
//    )
//
//    binding.recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
//    binding.recyclerView.adapter = adapter
//    binding.recyclerView.addViewHolderSpacing(16, requireContext())
}