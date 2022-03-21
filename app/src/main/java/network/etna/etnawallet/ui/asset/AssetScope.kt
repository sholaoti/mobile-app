package network.etna.etnawallet.ui.asset

import network.etna.etnawallet.ui.asset.assetinfo.AssetInfoViewModel
import network.etna.etnawallet.ui.asset.assetinfo.marketchart.MarketChartViewModel
import network.etna.etnawallet.ui.asset.assetinfo.marketdata.MarketDataViewModel
import network.etna.etnawallet.ui.asset.assetinfo.txshistory.TxsHistoryViewModel
import network.etna.etnawallet.ui.asset.receive.ReceiveViewModel
import network.etna.etnawallet.ui.asset.send.sendform.SendFormViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val assetModule = module {
    single { AssetScenarioModel() }
    viewModel { AssetInfoViewModel() }
    viewModel { MarketDataViewModel() }
    viewModel { MarketChartViewModel() }
    viewModel { TxsHistoryViewModel() }
    viewModel { ReceiveViewModel() }
}