package network.etna.etnawallet.ui.addwallet.importsinglewallet

import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentImportSingleWalletBinding
import network.etna.etnawallet.databinding.ViewholderBasicTabBinding
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.helpers.recycler.RxRecyclerAdapter
import network.etna.etnawallet.ui.helpers.recycler.addViewHolderHorizontalSpacing
import io.reactivex.Observable
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ImportSingleWalletFragment :
    BaseFragment<FragmentImportSingleWalletBinding>(
        FragmentImportSingleWalletBinding::inflate,
    ) {
    private val viewModel: ImportSingleWalletViewModel by sharedViewModel()

    override fun getTitleObservable(): Observable<String> {
        return Observable
            .just(resources
                .getString(
                    R.string.import_single_wallet,
                    viewModel.platform.name)
            )
    }

    private val layoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RxRecyclerAdapter(
            viewLifecycleOwner,
            R.layout.viewholder_basic_tab,
            ViewholderBasicTabBinding::bind,
            viewModel.getImportVariantsObservable(),
            { bindModel ->
                val binding = bindModel.binding
                val model = bindModel.model
                binding.textView.text = resources.getString(model.nameResourceId)
                binding.textView.setCompoundDrawablesRelativeWithIntrinsicBounds(model.iconResourceId, 0, 0, 0)
                binding.textView.isActivated = model.isActive
            },
            { selectModel ->
                val model = selectModel.model
                //layoutManager.smoothScrollToPosition(binding.recyclerView, null, selectModel.position)



                binding.recyclerView.post {
                    binding.recyclerView.smoothScrollToPosition(selectModel.position)
                }

                viewModel.setActive(model)
                binding.nestednav.findNavController().navigate(model.fragmentId)
            }
        )



        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addViewHolderHorizontalSpacing(16, requireContext())
    }
}