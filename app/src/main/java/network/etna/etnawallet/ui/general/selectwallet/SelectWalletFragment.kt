package network.etna.etnawallet.ui.general.selectwallet

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentSelectWalletBinding
import network.etna.etnawallet.databinding.ViewholderSelectWalletBinding
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.helpers.extensions.formatAmount
import network.etna.etnawallet.ui.helpers.recycler.RxRecyclerAdapter
import network.etna.etnawallet.ui.helpers.recycler.addViewHolderSpacing
import org.koin.androidx.viewmodel.ext.android.viewModel

class SelectWalletFragment :
    BaseFragment<FragmentSelectWalletBinding>(
        FragmentSelectWalletBinding::inflate,
        R.string.select_wallet
    ) {

    private val viewModel: SelectWalletViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RxRecyclerAdapter(
            viewLifecycleOwner,
            R.layout.viewholder_select_wallet,
            ViewholderSelectWalletBinding::bind,
            viewModel.getWalletModelsObservable(),
            { bindModel ->
                val binding = bindModel.binding
                val model = bindModel.model

                binding.root.isActivated = model.isActive
                binding.selectWalletIcon.isActivated = model.isActive
                binding.walletTypeTextView.isActivated = model.isActive

                binding.walletBalance.text = model.currencyBalance?.formatAmount()

                binding.walletTypeTextView.text = when {
                    model.isActive && model.blockchains.size == 1 ->
                        resources.getString(R.string.select_wallet_type_active_flatwallet)

                    model.isActive && model.blockchains.size > 1 ->
                        resources.getString(R.string.select_wallet_type_active_multiwallet)

                    !model.isActive && model.blockchains.size == 1 ->
                        resources.getString(R.string.select_wallet_type_flatwallet)

                    !model.isActive && model.blockchains.size > 1 ->
                        resources.getString(R.string.select_wallet_type_multiwallet)

                    else -> ""
                }

                binding.walletName.text = model.name
            },
            { selectModel ->
                val model = selectModel.model
                viewModel.selectActiveWallet(model.id)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addViewHolderSpacing(16, requireContext())
    }
}