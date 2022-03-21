package network.etna.etnawallet.ui.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import network.etna.etnawallet.R

abstract class BaseModalDialogFragment<VB: ViewBinding>(
    private val inflate: Inflate<VB>,
    private val titleResourceId: Int? = null
): BottomSheetDialogFragment() {

    private var _binding: VB? = null
    val binding get() = _binding!!

    val safeBinding: VB? get() = _binding

    override fun getTheme(): Int = R.style.TransparentModalBottomSheetDialog

    open fun getTitleObservable(): Observable<String> {
        return Observable.never()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val dialogView = inflater.inflate(R.layout.fragment_modal_dialog,container,false)
        val dialogContainer: ViewGroup = dialogView.findViewById(R.id.containerView)
        _binding = inflate.invoke(inflater, dialogContainer, true)
        return dialogView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.FragmentDialogAnimation
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        try {
            titleResourceId?.let {
                view.findViewById<TextView>(R.id.titleTextView).text = resources.getString(it)
            }

            getTitleObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    view.findViewById<TextView>(R.id.titleTextView).text = it
                }.disposedWith(viewLifecycleOwner)

        } catch (e: Exception) {
        }

        try {

            view.findViewById<View>(R.id.closeImageView).setOnClickListener {
                closeAction()
            }

        } catch (e: Exception) {
        }
    }

    open fun closeAction() {
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}