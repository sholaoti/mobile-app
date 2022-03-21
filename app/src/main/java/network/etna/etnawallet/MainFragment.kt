package network.etna.etnawallet

import network.etna.etnawallet.databinding.FragmentMainBinding
import network.etna.etnawallet.ui.base.BaseFragment

class MainFragment :
    BaseFragment<FragmentMainBinding>(
        FragmentMainBinding::inflate
    ) {

    override val isLoadingViewHolder: Boolean = true

}
