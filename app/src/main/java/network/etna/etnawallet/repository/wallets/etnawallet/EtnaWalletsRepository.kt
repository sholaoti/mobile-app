package network.etna.etnawallet.repository.wallets.etnawallet

import io.reactivex.Observable

interface EtnaWalletsRepository {
    fun getAssetsObservable(): Observable<List<BlockchainAsset>>
    fun getAssetObservable(assetId: AssetIdHolder): Observable<BlockchainAsset>
    fun getAsset(assetId: AssetIdHolder): BlockchainAsset?
    fun getActiveWalletObservable(): Observable<EtnaWalletModel>
    fun getWalletsObservable(): Observable<List<EtnaWalletModel>>
}