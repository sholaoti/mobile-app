package network.etna.etnawallet.ui.addwallet.importwallet

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentImportWalletBinding
import network.etna.etnawallet.databinding.ViewholderBasicProfileBinding
import network.etna.etnawallet.sdk.api.ImageProvider
import network.etna.etnawallet.ui.addwallet.importsinglewallet.ImportSingleWalletScenarioModel
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.helpers.extensions.dpToPx
import network.etna.etnawallet.ui.helpers.recycler.RxRecyclerAdapter
import network.etna.etnawallet.ui.helpers.recycler.addViewHolderSpacing
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ImportWalletFragment :
    BaseFragment<FragmentImportWalletBinding>(
        FragmentImportWalletBinding::inflate,
        R.string.import_wallet
    ) {

    private val viewModel: ImportWalletViewModel by viewModel()
    private val scenarioModel: ImportSingleWalletScenarioModel by inject()

    private val imageSize: Int by lazy {
        requireContext().dpToPx(32)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.importMultiWalletButton.setOnClickListener {
            findNavController().navigate(R.id.importWalletFragment_to_importMultiWalletFragment)
        }

        val adapter = RxRecyclerAdapter(
            viewLifecycleOwner,
            R.layout.viewholder_basic_profile,
            ViewholderBasicProfileBinding::bind,
            viewModel.getPlatformModelsObservable(),
            { bindModel ->
                val binding = bindModel.binding
                val model = bindModel.model

                Glide
                    .with(requireContext())
                    .load(ImageProvider.getPlatformImageURL(model.id, imageSize))
                    .into(binding.networkImageView)
                binding.textView.text = "${model.name} (${model.symbol})"
            },
            { selectModel ->
                scenarioModel.platform = selectModel.model
                findNavController().navigate(R.id.importWalletFragment_to_importSingleWalletFragment)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = adapter
        context?.let {
            binding.recyclerView.addViewHolderSpacing(16, it)
        }
    }
}