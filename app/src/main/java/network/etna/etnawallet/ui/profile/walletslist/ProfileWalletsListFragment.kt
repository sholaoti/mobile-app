package network.etna.etnawallet.ui.profile.walletslist

import android.os.Bundle
import android.view.*
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentProfileWalletsListBinding
import network.etna.etnawallet.databinding.ViewholderBasicProfileWalletBinding
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.helpers.extensions.toggleVisibilityGone
import network.etna.etnawallet.ui.helpers.recycler.RxRecyclerAdapter
import network.etna.etnawallet.ui.helpers.recycler.addViewHolderSpacing
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileWalletsListFragment :
    BaseFragment<FragmentProfileWalletsListBinding>(
        FragmentProfileWalletsListBinding::inflate,
        R.string.profile_my_wallets,
        R.menu.fragment_profile_wallets_list_menu
    ) {

    private val profileWalletsListViewModel: ProfileWalletsListViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RxRecyclerAdapter(
            viewLifecycleOwner,
            R.layout.viewholder_basic_profile_wallet,
            ViewholderBasicProfileWalletBinding::bind,
            profileWalletsListViewModel.getWalletsObservable(),
            { bindModel ->
                val binding = bindModel.binding
                val model = bindModel.model
                val walletDrawable = if (model.isActive) {
                    R.drawable.profile_wallets
                } else {
                    R.drawable.profile_wallet
                }
                binding.textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    walletDrawable, 0, 0, 0
                )
                binding.walletNameTextView.text = model.name
                binding.activeWalletTextView.toggleVisibilityGone(model.isActive)
            },
            { selectModel ->
                val model = selectModel.model
                val action =
                    ProfileWalletsListFragmentDirections.profileWalletsListFragmentToProfileWalletInfoFragment(
                        model.id
                    )
                findNavController().navigate(action)
            }
        )

        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = adapter
        context?.let {
            binding.recyclerView.addViewHolderSpacing(16, it)
        }
    }

    override fun menuItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_plus -> {
                activity?.findNavController(R.id.nav_host_fragment)
                    ?.navigate(R.id.profileFragment_to_importWalletActivity)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}