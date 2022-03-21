package network.etna.etnawallet.ui.profile.settings.activenetwork.testnetwarning

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.android.schedulers.AndroidSchedulers
import network.etna.etnawallet.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class TestnetWarningDialogFragment: BottomSheetDialogFragment() {

    private val viewModel: TestnetWarningViewModel by viewModel()

    override fun getTheme(): Int = R.style.TransparentBottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_testnet_warning, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.FragmentDialogAnimation
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        viewModel.showTestnetWarningObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                view.findViewById<View>(R.id.dontShowTextView)?.isActivated = !it
            }
            .disposedWith(viewLifecycleOwner)

        view.findViewById<View>(R.id.dontShowTextView)?.setOnClickListener {
            viewModel.invertShowTestnetWarning()
        }

        view.findViewById<View>(R.id.doneButton)?.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}