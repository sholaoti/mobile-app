package network.etna.etnawallet.ui.profile.settings.currency

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentProfileSettingsCurrencyBinding
import network.etna.etnawallet.databinding.ViewholderBasicProfileCurrencyBinding
import network.etna.etnawallet.sdk.network.icDrawableResource
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.helpers.recycler.RxRecyclerAdapter
import network.etna.etnawallet.ui.helpers.recycler.addViewHolderSpacing
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileSettingsCurrencyFragment :
    BaseFragment<FragmentProfileSettingsCurrencyBinding>(
        FragmentProfileSettingsCurrencyBinding::inflate,
        R.string.currency
    ) {

    private val viewModel: ProfileSettingsCurrencyViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RxRecyclerAdapter(
            viewLifecycleOwner,
            R.layout.viewholder_basic_profile_currency,
            ViewholderBasicProfileCurrencyBinding::bind,
            viewModel.getCurrenciesObservable(),
            { bindModel ->
                val binding = bindModel.binding
                val model = bindModel.model
                binding.textView.text = model.currency.name
                binding.textView.setCompoundDrawablesRelativeWithIntrinsicBounds(model.currency.icDrawableResource, 0, 0, 0)
                binding.radio.isActivated = model.isActive
            },
            { selectModel ->
                val model = selectModel.model
                viewModel.setActiveCurrency(model.currency)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = adapter
        context?.let {
            binding.recyclerView.addViewHolderSpacing(16, it)
        }
    }
}