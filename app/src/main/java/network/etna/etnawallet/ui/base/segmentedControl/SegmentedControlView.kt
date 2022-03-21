package network.etna.etnawallet.ui.base.segmentedControl

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.LayoutSegmentedControlBinding
import network.etna.etnawallet.databinding.LayoutSegmentedControlViewholderBinding
import network.etna.etnawallet.ui.helpers.extensions.dpToPx
import network.etna.etnawallet.ui.helpers.extensions.toggleVisibility
import network.etna.etnawallet.ui.helpers.recycler.BasicRecyclerAdapter
import network.etna.etnawallet.ui.helpers.recycler.RxViewHolder

class SegmentedControlView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private var _binding: LayoutSegmentedControlBinding? = null
    val binding get() = _binding!!

    var data: List<String> = emptyList()
        set(value) {
            field = value
            updateSpanSize()
            updateAdapter()
        }

    private val spanMargin: Float by lazy {
        context.dpToPx(4).toFloat()
    }

    private val spanHalfMargin: Float by lazy {
        spanMargin / 2
    }

    private val selectedPositionSubject: PublishSubject<Int> = PublishSubject.create()

    private var selectedPosition: Int = 0

    private var spanSize: Float = 0F
        set(value) {
            if (field != value) {
                field = value

                val layoutParams = FrameLayout.LayoutParams(
                    value.toInt(),
                    (binding.segmentedControlRecyclerView.measuredHeight - spanMargin).toInt())

                layoutParams.topMargin = spanHalfMargin.toInt()

                binding.selectedView.layoutParams = layoutParams
                binding.selectedView.x = (selectedPosition * spanSize) + (selectedPosition * 2 + 1) * spanHalfMargin
            }
        }

    init {
        _binding = LayoutSegmentedControlBinding.inflate(LayoutInflater.from(context), this, true)

        viewTreeObserver.addOnGlobalLayoutListener {
            updateSpanSize()
        }
    }

    private fun updateSpanSize() {
        spanSize = if (data.isNotEmpty()) {
            Log.d("SegmentedControl", "${binding.segmentedControlRecyclerView.measuredWidth.toFloat()} $spanMargin")

            binding.segmentedControlRecyclerView.measuredWidth.toFloat() / data.size - spanMargin
        } else {
            0F
        }
    }

    fun getSelectedPositionObservable(): Observable<Int> {
        return selectedPositionSubject
    }

    private fun updateAdapter() {
        val adapter = BasicRecyclerAdapter(
            R.layout.layout_segmented_control_viewholder,
            LayoutSegmentedControlViewholderBinding::bind,
            data,
            { model, binding, position ->
                binding.titleTextView.text = model
                binding.selectedTitleTextView.text = model
                binding.setSelected(position == selectedPosition)
            },
            { _, vb, position ->

                selectedPosition = position
                selectedPositionSubject.onNext(position)
                binding.segmentedControlRecyclerView.adapter?.itemCount?.let { itemCount ->
                    for (i in 0 until itemCount) {
                        binding.segmentedControlRecyclerView.findViewHolderForAdapterPosition(i)?.let {
                            val vh = it as RxViewHolder<LayoutSegmentedControlViewholderBinding>
                            vh.binding.setSelected(i == position)
                        }
                    }
                }

                //ANIMATE
                val newX = (position * spanSize) + (position * 2 + 1) * spanHalfMargin

                Log.d("SegmentedControl", "${binding.segmentedControlRecyclerView.measuredWidth} $spanSize $newX")

                ObjectAnimator
                    .ofFloat(
                        binding.selectedView, View.X,
                        binding.selectedView.x,
                        newX
                    )
                    .apply {
                        duration = 300
                        interpolator = DecelerateInterpolator(2F)
                    }.start()
            }
        )

        binding.segmentedControlRecyclerView.layoutManager = GridLayoutManager(context, data.size)
        binding.segmentedControlRecyclerView.adapter = adapter
    }

    fun setSelectedPosition(position: Int) {
        selectedPosition = position
        binding.selectedView.x = (position * spanSize) + (position * 2 + 1) * spanHalfMargin
        selectedPositionSubject.onNext(position)
    }
}

private fun LayoutSegmentedControlViewholderBinding.setSelected(isSelected: Boolean) {
    titleTextView.toggleVisibility(!isSelected)
    selectedTitleTextView.toggleVisibility(isSelected)
}