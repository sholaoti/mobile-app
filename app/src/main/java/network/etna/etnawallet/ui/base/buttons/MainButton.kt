package network.etna.etnawallet.ui.base.buttons

import network.etna.etnawallet.R
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import network.etna.etnawallet.databinding.LayoutMainButtonBinding
import network.etna.etnawallet.repository.warningservice.subscribeWithErrorHandler

enum class MainButtonState {
    NORMAL, LOADING, SUCCESS
}

class MainButton(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private var _binding: LayoutMainButtonBinding? = null
    val binding get() = _binding!!
    private var text: String = ""

    var state: MainButtonState = MainButtonState.NORMAL
        set(value) {
            if (field != value) {
                field = value
                stateChanged()
            }
        }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        binding.mainContainer.alpha = if (enabled) 1F else 0.5F
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MainButton)
        text = a.getString(R.styleable.MainButton_android_text) ?: ""
        val drawableStart = a.getDrawable(R.styleable.MainButton_android_drawableStart)
        val buttonColor = a.getString(R.styleable.MainButton_buttonColor)
        a.recycle()

        _binding = LayoutMainButtonBinding.inflate(LayoutInflater.from(context), this, true)

        when (buttonColor) {
            "0" -> { //ORANGE
            }
            "1" -> { //GREEN
                binding.mainContainer.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.main_button_green_background
                )
                binding.textView.setTextColor(resources.getColor(R.color.c_primaryColor900, null))
                binding.loadingIndicator.indeterminateDrawable.setTint(resources.getColor(R.color.c_primaryColor900, null))
                binding.successImageView.drawable.setTint(resources.getColor(R.color.c_primaryColor900, null))

            }
            "2" -> { // GRAY
                binding.mainContainer.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.main_button_gray_background
                )
            }
        }
        binding.textView.text = text

        drawableStart?.let {
            binding.textView.setCompoundDrawablesRelativeWithIntrinsicBounds(it, null, null, null)
        }
    }

    private fun stateChanged() {
        when (state) {
            MainButtonState.NORMAL -> {
                binding.loadingIndicator.visibility = View.INVISIBLE
                binding.successImageView.visibility = View.INVISIBLE
                binding.textView.visibility = View.VISIBLE
                //binding.textView.text = text
            }
            MainButtonState.LOADING -> {
                binding.loadingIndicator.visibility = View.VISIBLE
                binding.successImageView.visibility = View.INVISIBLE
                binding.textView.visibility = View.INVISIBLE
                //binding.textView.text = ""
            }
            MainButtonState.SUCCESS -> {
                binding.loadingIndicator.visibility = View.INVISIBLE
                binding.successImageView.visibility = View.VISIBLE
                binding.textView.visibility = View.INVISIBLE
                //binding.textView.text = ""
            }
        }
    }

    fun bind(
        completable: () -> Completable,
        lifecycleOwner: LifecycleOwner,
        onCompleted: (() -> Unit)? = null,
        onError: (() -> Unit)? = null,
        errorHandling: Boolean = true
    ) {

        setOnClickListener {
            if (state == MainButtonState.NORMAL) {
                state = MainButtonState.LOADING

                if (errorHandling) {
                    completable
                        .invoke()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWithErrorHandler(
                            {
                                state = MainButtonState.SUCCESS
                                onCompleted?.invoke()
                            },
                            {
                                state = MainButtonState.NORMAL
                                onError?.invoke()
                            }
                        )
                        .disposedWith(lifecycleOwner)
                } else {
                    completable
                        .invoke()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
                                state = MainButtonState.SUCCESS
                                onCompleted?.invoke()
                            },
                            {
                                state = MainButtonState.NORMAL
                                onError?.invoke()
                            }
                        )
                        .disposedWith(lifecycleOwner)
                }

            }
        }
    }
}