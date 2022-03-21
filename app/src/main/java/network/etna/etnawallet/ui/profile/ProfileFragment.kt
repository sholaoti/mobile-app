package network.etna.etnawallet.ui.profile

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentProfileBinding
import network.etna.etnawallet.ui.base.BaseFragment

class ProfileFragment :
    BaseFragment<FragmentProfileBinding>(
        FragmentProfileBinding::inflate,
        R.string.tabbar_profile
    ) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.myWalletsButton.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.profileFragmentToProfileWalletsListFragment())
        }

        binding.settingsTextView.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.profileFragmentToProfileSettingsFragment())
        }
    }
}