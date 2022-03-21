package network.etna.etnawallet.ui.asset.assetinfo

import android.os.Bundle
import androidx.transition.Transition
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import network.etna.etnawallet.R
import network.etna.etnawallet.extensions.prettyPrint
import network.etna.etnawallet.repository.loadingIndicatorService.LoadingIndicatorState
import network.etna.etnawallet.repository.wallets.etnawallet.BlockchainAssetType
import network.etna.etnawallet.sdk.api.etna.wssapi.txshistory.TxHistoryTransactionType
import network.etna.etnawallet.sdk.api.etna.wssapi.txshistory.valueStringPresentation
import network.etna.etnawallet.ui.asset.assetinfo.marketchart.MarketChartPeriodType
import network.etna.etnawallet.ui.asset.assetinfo.marketchart.MarketChartViewModel
import network.etna.etnawallet.ui.asset.assetinfo.marketdata.MarketDataViewModel
import network.etna.etnawallet.ui.asset.assetinfo.txshistory.TxsHistoryViewModel
import network.etna.etnawallet.ui.base.BaseFragment
import network.etna.etnawallet.ui.helpers.extensions.*
import network.etna.etnawallet.ui.helpers.recycler.RxRecyclerAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import network.etna.etnawallet.ui.helpers.recycler.addViewHolderSpacing
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.transition.Fade
import androidx.transition.TransitionManager
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import network.etna.etnawallet.databinding.*
import network.etna.etnawallet.repository.wallets.etnawallet.toJsonString
import network.etna.etnawallet.ui.base.BaseActivity

class AssetInfoFragment :
    BaseFragment<FragmentAssetInfoBinding>(
        FragmentAssetInfoBinding::inflate,
        null,
        R.menu.fragment_asset_info_menu
    ) {

    private val viewModel: AssetInfoViewModel by viewModel()
    private val marketViewModel: MarketDataViewModel by viewModel()
    private val marketChartViewModel: MarketChartViewModel by viewModel()
    private val txsHistoryViewModel: TxsHistoryViewModel by viewModel()

    private val imageSize: Int by lazy {
        requireContext().dpToPx(40)
    }

    private val screenHeight: Int by lazy {
        requireActivity().screenHeight()
    }

    private var guidelineY: Int = 0
        set(value) {
            if (field != value) {
                field = value
                val bottomHeight = screenHeight - value - dpToPx(24)
                bottomSheetPeekHeight.onNext(bottomHeight)
            }
        }

    private val layoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        guidelineY = binding.guideLineTopViewBottom.y.toInt()
    }

    private val bottomSheetPeekHeight: BehaviorSubject<Int> = BehaviorSubject.createDefault(0)

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            (activity as? BaseActivity)?.setSwipeRefreshEnabled(newState == BottomSheetBehavior.STATE_COLLAPSED)
            Log.d("AssetInfoFragment", "$newState")
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            safeBinding?.infoContainer?.alpha = 1 - slideOffset / 1.3F
        }
    }

    private var animateBottomSheet: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomSheet.layoutParams.height = screenHeight - dpToPx(70)

        binding.buttonSend.setOnClickListener {
            findNavController().navigate(AssetInfoFragmentDirections.toSendActivity(
                viewModel.getAssetIdHolder().toJsonString()
            ))
        }

        binding.buttonReceive.setOnClickListener {
            findNavController().navigate(AssetInfoFragmentDirections.toReceiveDialogFragment())
        }

        val behavior = BottomSheetBehavior
            .from(binding.bottomSheet)
        behavior.peekHeight = 0

        setupBottomSheetPeekHeightListener()

        setupAssetObservable()
        setupSegmentedControl()
        setupMarketData()
        setupTransactionsHistory()

        viewModel
            .getPlatformObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    try {
                        view.findViewById<Toolbar>(R.id.includeToolbar)
                            .menu
                            ?.findItem(R.id.action_wallet_settings)
                            ?.isVisible = it.supportedTokens.isNotEmpty()
                    } catch (e: Exception) {}
                },
                {}
            )
            .disposedWith(viewLifecycleOwner)
    }

    override fun onResume() {
        super.onResume()
        if (!viewModel.isAssetAvailable()) {
            activity?.finish()
        } else {
            binding.root.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
            guidelineY = binding.guideLineTopViewBottom.y.toInt()

            val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
            bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)
        }
    }

    override fun onPause() {
        super.onPause()
        binding.root.viewTreeObserver.removeOnGlobalLayoutListener(layoutListener)

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.removeBottomSheetCallback(bottomSheetCallback)
    }

    private fun setupBottomSheetPeekHeightListener() {
        Observable.combineLatest(
            bottomSheetPeekHeight,
            Observable.combineLatest(
                marketViewModel.getLoadingObservable(),
                marketChartViewModel.getLoadingObservable(),
                txsHistoryViewModel.getLoadingObservable())
                { l1, l2, l3 ->
                    l1 + l2 + l3
                }
                .distinctUntilChanged()
                .filter { it == LoadingIndicatorState.NORMAL })
            { peekHeight, loadingState ->
                Pair(peekHeight, loadingState)
            }
            .takeUntil { it.second == LoadingIndicatorState.NORMAL }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ pair ->
                var peekHeight = pair.first
                val loadingState = pair.second

                if (loadingState != LoadingIndicatorState.NORMAL) {
                    peekHeight = 0
                }

                Log.d("AssetInfoFragment", "$peekHeight $loadingState")


                val behavior = BottomSheetBehavior
                    .from(binding.bottomSheet)

                if (animateBottomSheet) {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
                behavior.peekHeight = peekHeight
                if (animateBottomSheet) {
                    behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    animateBottomSheet = false
                }
            }, {})
            .disposedWith(viewLifecycleOwner)
    }

    private fun setupAssetObservable() {
        viewModel
            .getAssetObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    val model = it
                    binding.assetTitleTextView.text =
                        resources.getString(R.string.asset_info_balance_title, model.name)

                    val assetType = when (model.type) {
                        BlockchainAssetType.CURRENCY -> resources.getString(R.string.blockchain_asset_type_currency)
                        BlockchainAssetType.TOKEN -> resources.getString(R.string.blockchain_asset_type_token)
                    }

                    binding.assetSubtitleTextView.text =
                        resources.getString(
                            R.string.asset_info_balance_subtitle,
                            assetType,
                            model.networkName
                        )

                    binding.currencyBalanceTextView.text = model.currencyBalance?.formatAmount()

                    binding.balanceTextView.text =
                        resources.getString(
                            R.string.asset_info_balance_balance,
                            model.balance?.formatAmountRounded() ?: "-",
                            model.name
                        )

                    model.change24h?.apply {
                        if (this >= 0) {
                            binding.balanceArrowUp.visibility = View.VISIBLE
                            binding.balanceArrowDown.visibility = View.INVISIBLE
                            binding.change24TextView.isActivated = true
                        } else {
                            binding.balanceArrowUp.visibility = View.INVISIBLE
                            binding.balanceArrowDown.visibility = View.VISIBLE
                            binding.change24TextView.isActivated = false
                        }

                        binding.change24TextView.visibility = View.VISIBLE
                        binding.change24TextView.text = this.format24Change()

                    } ?: run {
                        binding.balanceArrowDown.visibility = View.GONE
                        binding.balanceArrowUp.visibility = View.GONE
                        binding.change24TextView.visibility = View.GONE
                    }

                    Glide
                        .with(requireContext())
                        .load(model.iconURLProvider(imageSize))
                        .into(binding.assetImageView)
                },
                {
                    val x = 1
                })
            .disposedWith(viewLifecycleOwner)
    }

    private fun setupSegmentedControl() {
        binding.segmentedControl.data =
            listOf(
                resources.getString(R.string.asset_info_market_title),
                resources.getString(R.string.asset_info_history_title)
            )

        binding.segmentedControl
            .getSelectedPositionObservable()
            .subscribe {

                val transition: Transition = Fade()
                transition.duration = 500
                transition.addTarget(R.id.marketDataContainer)
                transition.addTarget(R.id.transactionsContainer)
                transition.addTarget(R.id.marketOverviewTitle)
                transition.addTarget(R.id.transactionHistoryTitle)

                TransitionManager.beginDelayedTransition(binding.dataViewGroup, transition)

                when (it) {
                    0 -> {
                        binding.marketOverviewTitle.visibility = View.VISIBLE
                        binding.transactionHistoryTitle.visibility = View.GONE
                        binding.marketDataContainer.visibility = View.VISIBLE
                        binding.transactionsContainer.visibility = View.GONE
                    }
                    1 -> {
                        binding.marketOverviewTitle.visibility = View.GONE
                        binding.transactionHistoryTitle.visibility = View.VISIBLE
                        binding.marketDataContainer.visibility = View.GONE
                        binding.transactionsContainer.visibility = View.VISIBLE
                    }
                }
            }
            .disposedWith(viewLifecycleOwner)
    }

    private fun setupMarketData() {
        val adapter = AssetInfoMarketRecyclerAdapter(
            viewLifecycleOwner,
            marketViewModel
                .getDataObservable()
                .observeOn(AndroidSchedulers.mainThread())
        )

        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addViewHolderSpacing(16, requireContext())

        binding.lineChart.apply {

            setTouchEnabled(false)

            xAxis.isEnabled = false

            axisLeft.apply {
                setDrawAxisLine(false)
                setDrawGridLines(false)

                setLabelCount(6, true)

                setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)

                textColor = resources.getColor(R.color.c_primaryColor400, null)
            }

            axisRight.isEnabled = false

            setDrawGridBackground(false)

            legend.isEnabled = false
            description.isEnabled = false
        }

        marketChartViewModel
            .getMarketChartInfoObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { marketChartInfo ->

                binding.priceTextView.text = marketChartInfo.price.formatAmount()

                binding.percentTextView.text = marketChartInfo.percent.format24Change(true, 2)
                binding.percentTextView.isActivated = marketChartInfo.isPositive

                val vl = marketChartInfo.lineDataSet

                vl.setDrawValues(false)
                vl.setDrawFilled(true)

                vl.mode = LineDataSet.Mode.CUBIC_BEZIER
                vl.cubicIntensity = 0.1f
                vl.setDrawCircles(false)
                vl.lineWidth = 2f

                if (marketChartInfo.isPositive) {
                    vl.fillDrawable = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.linechart_fade_fill_green
                    )

                    vl.color = resources.getColor(R.color.c_secondaryColorGreen, null)
                } else {
                    vl.fillDrawable = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.linechart_fade_fill_red
                    )

                    vl.color = resources.getColor(R.color.c_secondaryColorRed, null)
                }

                setupXAxisCaps(marketChartInfo.xAxisCaps)

                binding.lineChart.axisLeft.apply {
                    setValueFormatter { value, _ ->
                        (value + marketChartInfo.correction).prettyPrint()
                    }
                }

                binding.lineChart.data = LineData(vl)
                //binding.lineChart.invalidate()
                binding.lineChart.animateY(500, Easing.EasingOption.EaseOutCubic)

            }
            .disposedWith(viewLifecycleOwner)

        binding.segmentedControlPeriod.data =
            listOf(
                resources.getString(R.string.graph_period_day),
                resources.getString(R.string.graph_period_week),
                resources.getString(R.string.graph_period_month),
                resources.getString(R.string.graph_period_year)
            )

        binding.segmentedControlPeriod.setSelectedPosition(3)

        binding.segmentedControlPeriod
            .getSelectedPositionObservable()
            .subscribe {
                val type = when (it) {
                    0 -> MarketChartPeriodType.DAY
                    1 -> MarketChartPeriodType.WEEK
                    2 -> MarketChartPeriodType.MONTH
                    3 -> MarketChartPeriodType.YEAR
                    else -> throw Exception("unsupported")
                }
                marketChartViewModel.updateMarketChartPeriodType(type)
            }
            .disposedWith(viewLifecycleOwner)
    }

    private fun setupXAxisCaps(data: List<String>) {
        val row = binding.tableRow
        row.removeAllViews()

        data.forEachIndexed { index, s ->
            val tvBinding = ViewholderChartCapsBinding.inflate(
                LayoutInflater.from(requireContext()),
                row,
                false
            )
            tvBinding.textView.text = s
            row.addView(tvBinding.root)

            if (index < data.size - 1) {
                val separatorBinding = ViewholderChartCapsSpaceBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    row,
                    false
                )
                row.addView(separatorBinding.root)
            }
        }

        binding.table.isStretchAllColumns = false
        binding.table.isShrinkAllColumns = false
        for (i in data.indices.drop(1)) {
            val index = i * 2 - 1
            binding.table.setColumnStretchable(index, true)
            binding.table.setColumnShrinkable(index, true)
        }
    }

    private fun setupTransactionsHistory() {
        val transactionsAdapter = RxRecyclerAdapter(
            viewLifecycleOwner,
            R.layout.viewholder_asset_transaction,
            ViewholderAssetTransactionBinding::bind,
            txsHistoryViewModel.getDataObservable(),
            { bindModel ->
                val binding = bindModel.binding
                val model = bindModel.model
                binding.amountTextView.text = model.type?.name

                binding.failedImageView.toggleVisibility(!model.success)

                binding.buyImageView.toggleVisibility(model.success && model.type == TxHistoryTransactionType.Receive)
                binding.sellImageView.toggleVisibility(model.success && model.type == TxHistoryTransactionType.Send)

                binding.statusSuccessTextView.toggleVisibility(model.success)
                binding.statusFailedTextView.toggleVisibility(!model.success)

                model.valueStringPresentation()?.apply {
                    binding.amountTextView.visibility = View.VISIBLE
                    binding.amountTextView.isActivated = isPositive
                    binding.amountTextView.text = text
                } ?: run {
                    binding.amountTextView.visibility = View.GONE
                }

                binding.titleTextView.text = when (model.type) {
                    TxHistoryTransactionType.Send -> resources.getString(
                        R.string.tx_title_sell,
                        model.currency
                    )
                    TxHistoryTransactionType.Receive -> resources.getString(
                        R.string.tx_title_buy,
                        model.currency
                    )
                    null -> model.currency
                }

                binding.subtitleTextView.text = model.date.prettyPrint(requireContext())
            }
        )

        binding.transactionsRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.transactionsRecyclerView.adapter = transactionsAdapter
    }

    override fun menuItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_wallet_settings -> {
                findNavController().navigate(AssetInfoFragmentDirections.toWalletPlatformSettingsFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}