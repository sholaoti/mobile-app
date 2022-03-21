package network.etna.etnawallet.ui.base.tokenstack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.ViewholderAssetBinding
import network.etna.etnawallet.repository.wallets.etnawallet.BlockchainAsset
import network.etna.etnawallet.repository.wallets.etnawallet.BlockchainAssetType
import network.etna.etnawallet.ui.helpers.extensions.dpToPx
import network.etna.etnawallet.ui.helpers.extensions.formatAmount
import network.etna.etnawallet.ui.helpers.extensions.formatAmountRounded
import java.math.BigDecimal

class AssetViewHolder(private val parent: ViewGroup) {

    private var _binding: ViewholderAssetBinding? = null
    val binding get() = _binding!!

    private val imageSize: Int by lazy {
        parent.context.dpToPx(32)
    }

    init {
        _binding = ViewholderAssetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parent.context.dpToPx(136))
        binding.root.layoutParams = lp
    }

    fun bind(model: BlockchainAsset) {
        binding.tokenNameTextView.text = model.name

        var description: String = when (model.type) {
            BlockchainAssetType.CURRENCY -> parent.context.resources.getString(R.string.blockchain_asset_type_currency)
            BlockchainAssetType.TOKEN -> parent.context.resources.getString(R.string.blockchain_asset_type_token)
        }

        model.networkName?.let {
            description += " | $it"
        }

        binding.netNameTextView.text = description
        binding.tokenBalanceTextView.text = model.balance?.formatAmountRounded() ?: "-"

        val currency24hChange = model.currency24hChange
        if (currency24hChange != null) {
            if (currency24hChange.amount >= BigDecimal.ZERO) {
                binding.tokenChange24TextView.isActivated = true
                binding.tokenChange24TextView.text = model.currencyBalance?.formatAmount()
                binding.balanceArrowUp.visibility = View.VISIBLE
                binding.balanceArrowDown.visibility = View.INVISIBLE
            } else {
                binding.tokenChange24TextView.isActivated = false
                binding.tokenChange24TextView.text = model.currencyBalance?.formatAmount()
                binding.balanceArrowUp.visibility = View.INVISIBLE
                binding.balanceArrowDown.visibility = View.VISIBLE
            }
        } else {
            binding.tokenChange24TextView.text = null
            binding.balanceArrowUp.visibility = View.INVISIBLE
            binding.balanceArrowDown.visibility = View.INVISIBLE
        }

        Glide
            .with(parent.context)
            .load(model.iconURLProvider(imageSize))
            .into(binding.tokenImageView)
    }
}