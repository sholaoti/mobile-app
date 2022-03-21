package network.etna.etnawallet.ui.profile.settings

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentProfileSettingsBinding
import network.etna.etnawallet.repository.wallets.networkset.NetworkSet
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.profile.settings.activenetwork.humanReadableNameResource
import network.etna.etnawallet.ui.profile.settings.activenetwork.icDrawableResource
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileSettingsFragment :
    BaseFragment<FragmentProfileSettingsBinding>(
        FragmentProfileSettingsBinding::inflate,
        R.string.settings
    ) {

    private val viewModel: ProfileSettingsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel
            .getActiveCurrencyObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.activeCurrencyTextView.text = it.name
            }
            .disposedWith(viewLifecycleOwner)

        viewModel
            .getActiveNetworkSetObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { networkSet ->
                binding.activeNetworkTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(networkSet.icDrawableResource, 0, 0, 0)
                binding.selectedActiveNetworkName.text = resources.getString(R.string.net_active, resources.getString(networkSet.humanReadableNameResource))
                binding.selectedActiveNetworkName.isActivated = networkSet == NetworkSet.MAINNET
            }
            .disposedWith(viewLifecycleOwner)

        binding.activeNetworkTextView.setOnClickListener {
            findNavController().navigate(ProfileSettingsFragmentDirections.profileSettingsFragmentToProfileSettingsActiveNetworkFragment())
        }

        binding.currencyTextView.setOnClickListener {
            findNavController().navigate(ProfileSettingsFragmentDirections.profileSettingsFragmentToProfileSettingsCurrencyFragment())
        }
    }
}