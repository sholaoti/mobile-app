package network.etna.etnawallet.ui.base.warning

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import network.etna.etnawallet.databinding.LayoutWarningViewBinding
import org.koin.core.component.KoinComponent

class WarningView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs), KoinComponent {

    private var _binding: LayoutWarningViewBinding? = null
    val binding get() = _binding!!

    init {
        _binding = LayoutWarningViewBinding.inflate(LayoutInflater.from(context), this, true)
    }
}