package network.etna.etnawallet.ui.login.pincode

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import network.etna.etnawallet.databinding.FragmentPinCodeBinding
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.helpers.extensions.shake
import network.etna.etnawallet.ui.helpers.extensions.toggleVisibilityGone
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

abstract class PinCodeFragment:
    BaseFragment<FragmentPinCodeBinding>(
        FragmentPinCodeBinding::inflate
    ) {

    abstract fun getViewModel(): PinCodeViewModel
    abstract fun getTitleText(): String
    abstract fun getSubtitleTextObservable(): Observable<String>
    abstract fun pinCodeSuccess()

    open fun getSecondaryActionResourceId(): Int? {
        return null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getViewModel().clearPin()

        listOf(binding.dot1, binding.dot2, binding.dot3, binding.dot4, binding.dot5, binding.dot6)
            .forEachIndexed { index, pinCodeDotView ->
                pinCodeDotView.toggleVisibilityGone(index < getViewModel().pinCodeMaxLength)
            }

        binding.title.text = getTitleText()

        getSubtitleTextObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                safeBinding?.subtitle?.text = it
                safeBinding?.subtitle?.toggleVisibilityGone(it.isNotEmpty())
            }
            .disposedWith(viewLifecycleOwner)

        val secondaryActionResourceId = getSecondaryActionResourceId()
        if (secondaryActionResourceId != null) {
            binding.pinCodeButtonsView.binding.buttonSecondary.setImageResource(secondaryActionResourceId)
        } else {
            binding.pinCodeButtonsView.binding.buttonSecondary.visibility = View.INVISIBLE
        }

        binding.pinCodeButtonsView
            .getActionObservable()
            .subscribe { action ->
                when (action) {
                    is PinCodeButtonAction.Button -> {
                        getViewModel().digitPressed(action.digit)
                    }
                    PinCodeButtonAction.Clear -> {
                        getViewModel().clearPressed()
                    }
                }
            }
            .disposedWith(viewLifecycleOwner)

        Observable.combineLatest(
            getViewModel().getFragmentStateObservable(),
            getViewModel().getPinCodeLengthObservable())
            { fragmentState, pinCodeLength ->
                Pair(fragmentState, pinCodeLength)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { pair ->
                val fragmentState = pair.first
                val pinCodeLength = pair.second

                var activeDotState: PinCodeDotViewState = PinCodeDotViewState.PRESSED

                if (fragmentState == PinCodeFragmentState.SUCCESS) {
                    activeDotState = PinCodeDotViewState.SUCCESS
                }

                listOf(binding.dot1, binding.dot2, binding.dot3, binding.dot4, binding.dot5, binding.dot6)
                    .forEachIndexed { index, pinCodeDotView ->
                        if (index < pinCodeLength) {
                            pinCodeDotView.changeState(activeDotState)
                        } else {
                            pinCodeDotView.changeState(PinCodeDotViewState.NORMAL)
                        }
                    }

                when (fragmentState) {
                    PinCodeFragmentState.DONE -> {
                        Handler(Looper.getMainLooper()).postDelayed({
                            pinCodeSuccess()
                        }, 200)
                    }
                    PinCodeFragmentState.SUCCESS -> {
                        Handler(Looper.getMainLooper()).postDelayed({
                            pinCodeSuccess()
                        }, 400)
                    }
                    PinCodeFragmentState.ERROR -> {
                        if (pinCodeLength != 0) {
                            binding.pinCodeDotsView.shake {
                                getViewModel().clearPin()
                            }
                        }
                    }
                    PinCodeFragmentState.NORMAL -> {}
                }
            }
            .disposedWith(viewLifecycleOwner)
    }
}