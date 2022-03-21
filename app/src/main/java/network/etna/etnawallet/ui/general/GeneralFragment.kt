package network.etna.etnawallet.ui.general

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.navigation.fragment.findNavController
import network.etna.etnawallet.R
import network.etna.etnawallet.databinding.FragmentGeneralBinding
import network.etna.etnawallet.ui.base.BaseFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import network.etna.etnawallet.repository.loadingIndicatorService.LoadingIndicatorState
import network.etna.etnawallet.repository.wallets.etnawallet.BlockchainAssetType
import network.etna.etnawallet.ui.base.BaseActivity
import network.etna.etnawallet.ui.base.tokenstack.*
import network.etna.etnawallet.ui.helpers.extensions.dpToPx
import network.etna.etnawallet.ui.helpers.extensions.format24Change
import network.etna.etnawallet.ui.helpers.extensions.formatAmount
import network.etna.etnawallet.ui.helpers.extensions.screenHeight
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import java.util.concurrent.TimeUnit

class GeneralFragment :
    BaseFragment<FragmentGeneralBinding>(
        FragmentGeneralBinding::inflate,
        R.string.tabbar_general
    ) {

    private val viewModel: GeneralFragmentViewModel by viewModel()

    private val bottomSheetMaxPeekHeight: BehaviorSubject<Optional<Int>> =
        BehaviorSubject.createDefault(Optional.empty())

    private val tokensCountSubject: BehaviorSubject<Optional<Int>> =
        BehaviorSubject.createDefault(Optional.empty())

    private val screenHeight: Int by lazy {
        requireActivity().screenHeight()
    }

    private val tokenHeight: Int by lazy {
        requireContext().resources.getDimension(R.dimen.token_view_height).toInt()
    }

    private val bottomSheetMaxHeight: Int by lazy {
        screenHeight - requireContext().resources.getDimension(R.dimen.general_fragment_bottom_sheet_expanded_top_margin).toInt()
    }

    private var guidelineY: Int = 0
        set(value) {
            if (field != value) {
                field = value
                val bottomHeight = screenHeight - value
                bottomSheetMaxPeekHeight.onNext(Optional.of(bottomHeight))
            }
        }

    private val layoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        guidelineY = binding.guideLineTopViewBottom.y.toInt()
    }

    private val loadingSubject: BehaviorSubject<LoadingIndicatorState> =
        BehaviorSubject.createDefault(LoadingIndicatorState.LOADING)

    private val bottomSheetState: PublishSubject<Int> = PublishSubject.create()
    private val bottomSheetBounceModeSubject: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    override fun getLoadingIndicatorStateObservable(): Observable<LoadingIndicatorState> {
        return loadingSubject
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel
            .getActiveWalletObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { model ->
                binding.walletTextView.text = if (model.isMainnet) {
                    model.name
                } else {
                    resources.getString(R.string.wallet_name_testnet, model.name)
                }

                binding.balanceTextView.text = model.currencyBalance?.formatAmount()
                model.currency24hChange?.format24Change()?.apply {
                    binding.differenceTextView.visibility = View.VISIBLE
                    binding.differenceTextView.text = text
                    binding.differenceTextView.isActivated = isPositive
                } ?: run {
                    binding.differenceTextView.visibility = View.INVISIBLE
                }
            }
            .disposedWith(viewLifecycleOwner)


        binding.bottomSheet.adapter = TokenStackAdapter(
            viewModel
                .getAssetsObservable()
                .doOnNext {
                    tokensCountSubject.onNext(Optional.of(it.size))
                    loadingSubject.onNext(LoadingIndicatorState.NORMAL)
                }
        ) {
            findNavController().navigate(
                GeneralFragmentDirections.toAssetActivity(
                    it.type == BlockchainAssetType.CURRENCY,
                    it.platform,
                    it.id,
                    it.referenceId,
                    it.address
                )
            )
        }

        binding.selectWalletClickableArea.setOnClickListener {
            findNavController().navigate(GeneralFragmentDirections.generalFragmentToSelectWalletFragment())
        }

        binding.nftTextView.setOnClickListener {
            findNavController().navigate(GeneralFragmentDirections.toNftCollectionsFragment())
        }

        binding.bottomSheet.layoutParams.height = bottomSheetMaxHeight

        val behavior = BottomSheetBehavior.from(binding.bottomSheet)
        behavior.peekHeight = 0

        behavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.bottomSheet.isBounceAllowed = newState != BottomSheetBehavior.STATE_EXPANDED
                bottomSheetState.onNext(newState)
                (activity as? BaseActivity)?.setSwipeRefreshEnabled(newState == BottomSheetBehavior.STATE_COLLAPSED)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        Observable.combineLatest(
            bottomSheetState,
            binding.bottomSheet.getMouseUpObservable(),
            bottomSheetBounceModeSubject)
            { state, mouseUp, bottomSheetBounceMode ->
                Triple(state, mouseUp, bottomSheetBounceMode)
            }
            .subscribe {
                val state = it.first
                val mouseUp = it.second
                val bottomSheetBounceMode = it.third

                if (bottomSheetBounceMode && mouseUp && state != BottomSheetBehavior.STATE_COLLAPSED) {
                    BottomSheetBehavior
                        .from(binding.bottomSheet)
                        .state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
            .disposedWith(viewLifecycleOwner)

        setupBottomSheetPeekHeightListener()
    }


    override fun onResume() {
        super.onResume()
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
        guidelineY = binding.guideLineTopViewBottom.y.toInt()
    }

    override fun onPause() {
        super.onPause()
        binding.root.viewTreeObserver.removeOnGlobalLayoutListener(layoutListener)
    }

    private fun setupBottomSheetPeekHeightListener() {

        Observable.combineLatest(
            bottomSheetMaxPeekHeight
                .filter { it.isPresent }
                .map { it.get() },
            tokensCountSubject
                .filter { it.isPresent }
                .map { it.get() },
            loadingSubject
                .delay(300, TimeUnit.MILLISECONDS)
                .filter { it == LoadingIndicatorState.NORMAL })
            { maxPeekHeight, tokensCount, loadingState ->

                val tokensHeight = tokenHeight * (tokensCount + 1)

                val peekHeight = when {
                    loadingState != LoadingIndicatorState.NORMAL -> 0
                    tokensHeight < maxPeekHeight -> tokensHeight
                    else -> maxPeekHeight
                }

                val bottomSheetHeight = when {
                    tokensHeight < bottomSheetMaxHeight -> tokensHeight
                    else -> bottomSheetMaxHeight
                }

                Triple(peekHeight, bottomSheetHeight, loadingState)
            }
            .distinctUntilChanged()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ triple ->
                val peekHeight = triple.first
                val bottomSheetHeight = triple.second
                val loadingState = triple.third

                Log.d("GeneralFragment", "$peekHeight $bottomSheetHeight $loadingState")

                if (peekHeight < bottomSheetHeight) {
                    binding.bottomSheet.layoutParams.height = bottomSheetHeight
                    bottomSheetBounceModeSubject.onNext(false)
                } else {
                    binding.bottomSheet.layoutParams.height = bottomSheetHeight + dpToPx(40)
                    bottomSheetBounceModeSubject.onNext(true)
                }

                val behavior = BottomSheetBehavior
                    .from(binding.bottomSheet)

                if (behavior.peekHeight == 0 && peekHeight > 0) {
                    behavior.peekHeight = peekHeight
                    binding.bottomSheet.y = binding.bottomSheet.y + peekHeight
                    binding.bottomSheet.animate().translationY(0f)
                } else {
                    behavior.peekHeight = peekHeight
                }
            }, {})
            .disposedWith(viewLifecycleOwner)
    }
}