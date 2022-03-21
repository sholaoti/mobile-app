package network.etna.etnawallet.ui.profile.settings.activenetwork

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentProfileSettingsActiveNetworkBinding
import network.etna.etnawallet.databinding.ViewholderBasicProfileCurrencyBinding
import network.etna.etnawallet.repository.wallets.networkset.NetworkSet
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.helpers.recycler.RxRecyclerAdapter
import network.etna.etnawallet.ui.helpers.recycler.addViewHolderSpacing
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileSettingsActiveNetworkFragment :
    BaseFragment<FragmentProfileSettingsActiveNetworkBinding>(
        FragmentProfileSettingsActiveNetworkBinding::inflate,
        R.string.active_network
    ) {

    private val viewModel: ProfileSettingsActiveNetworkViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RxRecyclerAdapter(
            viewLifecycleOwner,
            R.layout.viewholder_basic_profile_currency,
            ViewholderBasicProfileCurrencyBinding::bind,
            viewModel.getNetworkSetModelsObservable(),
            { bindModel ->
                val binding = bindModel.binding
                val model = bindModel.model
                binding.textView.text = resources.getString(model.network.humanReadableNameResource)
                binding.textView.setCompoundDrawablesRelativeWithIntrinsicBounds(model.network.icDrawableResource, 0, 0, 0)
                binding.radio.isActivated = model.isActive
            },
            { selectModel ->
                val model = selectModel.model
                viewModel.selectActiveNetworkSet(model.network)
                if (model.network == NetworkSet.TESTNET && viewModel.showTestnetWarning()) {
                    findNavController().navigate(ProfileSettingsActiveNetworkFragmentDirections.toTestnetWarningDialogFragment())
                }
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addViewHolderSpacing(16, requireContext())
    }
}