package network.etna.etnawallet.ui.base.tokenstack

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.allViews
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import network.etna.etnawallet.R

class TokenStackLayout : NestedScrollView {

    private var mAdapter: TokenStackAdapter? = null

    var frame: FrameLayout? = null
        private set

    var adapter: TokenStackAdapter?
        get() = mAdapter
        set(adapter) {
            this.mAdapter = adapter
            mAdapter?.setAdapterParams(this)
            setupPositionListener()
        }

    private val mouseUpSubject: PublishSubject<Boolean> = PublishSubject.create()

    private val getPositionFunctionSubject: BehaviorSubject<GetPositionFunctionVariant> =
        BehaviorSubject.createDefault(GetPositionFunctionVariant.OriginalNoAnimate)

    private val cellHeight: Int = context.resources.getDimension(R.dimen.token_view_height).toInt()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet
    ) : super(context, attrs) {
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init()
    }


    private var changeSubjectObservableDisposable: Disposable? = null

    private fun init() {
        isFillViewport = true
        isVerticalScrollBarEnabled = false

        frame = TokenStackFrameLayout(this)
        addView(
            frame,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        frame?.clipChildren = false
    }

    private fun setupPositionListener() {
        changeSubjectObservableDisposable?.dispose()

        changeSubjectObservableDisposable = Flowable.combineLatest(
            getPositionFunctionSubject
                .distinctUntilChanged()
                .toFlowable(BackpressureStrategy.LATEST),
            adapter!!
                .getViewsObservable()
                .toFlowable(BackpressureStrategy.LATEST)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { views ->
                    frame?.apply {
                        views.forEach {
                            if (!allViews.contains(it)) {
                                addView(it)
                            }
                            it.bringToFront()
                        }
                    }
                    assetViews = views
                })
            { getPositionFunction, views ->
                Pair(getPositionFunction, views)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { pair ->
                    val getPositionFunctionVariant = pair.first
                    val views = pair.second

                    views.forEachIndexed { index, view ->

                        val originalY = (cellHeight * index).toFloat()
                        val positionResult = getPositionFunctionVariant.function(index, originalY)
                        positionManager.moveViewTo(
                            view,
                            positionResult.first,
                            positionResult.second
                        )
                    }
                },
                {
                    val x = 1
                }
            )

        findViewTreeLifecycleOwner()?.let {
            changeSubjectObservableDisposable?.disposedWith(it)
        }
    }

    private val positionManager = ViewPositionManager()

    private var isVerticalNestedPreScrollAllowed: Boolean = false
    private var verticalNestedPreScrollChangeY: Float = 0F
    private var preventTokensCollapse: Boolean = false

    var isBounceAllowed: Boolean = true
        set(value) {
            if (field != value) {
                field = value
            }
            if (value) {
                preventTokensCollapse = true
            }
            if (!value) {
                verticalNestedPreScrollChangeY = 0F
            }
        }

    fun onVerticalNestedPreScroll(dy: Int) {
        if (!isVerticalNestedPreScrollAllowed || !isBounceAllowed) {
            return
        }

        verticalNestedPreScrollChangeY += dy

        val capturedVerticalNestedPreScrollChangeY = verticalNestedPreScrollChangeY

        getPositionFunctionSubject.onNext(GetPositionFunctionVariant.Bouncing(capturedVerticalNestedPreScrollChangeY))
    }

    private var assetViews: List<View> = emptyList()

    override fun onScrollChanged(scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        adapter?.let {

            if (preventTokensCollapse) {
                scrollTo(0, 0)
                getPositionFunctionSubject.onNext(GetPositionFunctionVariant.OriginalAnimate)
            } else {

                assetViews.forEachIndexed { index, view ->
                    val originalY = (cellHeight * index).toFloat()
                    if (originalY < scrollY) {
                        view.y = scrollY.toFloat()
                    } else {
                        view.y = originalY
                    }
                }

                //getPositionFunctionSubject.onNext(GetPositionFunctionVariant.Collapsing(scrollY))
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (!isBounceAllowed) {
                    preventTokensCollapse = false
                }
                verticalNestedPreScrollChangeY = 0F
                isVerticalNestedPreScrollAllowed = true
                mouseUpSubject.onNext(false)
            }
            MotionEvent.ACTION_UP -> {
                isVerticalNestedPreScrollAllowed = false
                if (isBounceAllowed) {
                    resetTokenPositions()
                }
                mouseUpSubject.onNext(true)
            }
            MotionEvent.ACTION_CANCEL -> {
                isVerticalNestedPreScrollAllowed = false
                if (isBounceAllowed) {
                    resetTokenPositions()
                }
                mouseUpSubject.onNext(true)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    fun getMouseUpObservable(): Observable<Boolean> {
        return mouseUpSubject
    }

    private fun resetTokenPositions() {
        getPositionFunctionSubject.onNext(GetPositionFunctionVariant.OriginalAnimate)
    }
}
