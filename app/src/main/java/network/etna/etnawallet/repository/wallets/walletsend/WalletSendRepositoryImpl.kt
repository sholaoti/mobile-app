package network.etna.etnawallet.repository.wallets.walletsend

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.reactivex.Completable
import io.reactivex.Single
import network.etna.etnawallet.repository.blockchainnetworks.BlockchainNetworksRepository
import network.etna.etnawallet.repository.wallets.cryptowallet.CryptoWalletsRepository
import network.etna.etnawallet.repository.wallets.cryptowalletcredentials.CryptoWalletCredentialsRepository
import network.etna.etnawallet.repository.wallets.cryptowalletcredentials.SignResult
import network.etna.etnawallet.repository.wallets.cryptowalletcredentials.toJsonObject
import network.etna.etnawallet.repository.wallets.etnawallet.EtnaWalletsRepository
import network.etna.etnawallet.sdk.api.etna.wssapi.EtnaWSSAPI
import network.etna.etnawallet.sdk.api.etna.wssapi.preparetransaction.PrepareTransactionError
import network.etna.etnawallet.sdk.api.etna.wssapi.preparetransaction.prepareTransaction
import network.etna.etnawallet.sdk.api.etna.wssapi.sendtransaction.sendTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WalletSendRepositoryImpl: KoinComponent, WalletSendRepository {

    private val etnaWSSAPI: EtnaWSSAPI by inject()
    private val etnaWalletsRepository: EtnaWalletsRepository by inject()
    private val cryptoWalletsRepository: CryptoWalletsRepository by inject()
    private val cryptoWalletCredentialsRepository: CryptoWalletCredentialsRepository by inject()
    private val blockchainNetworksRepository: BlockchainNetworksRepository by inject()

    override fun sendTransaction(model: WalletSendModel): Completable {

        return Single.zip(
            cryptoWalletsRepository
                .getWalletsObservable()
                .take(1)
                .singleOrError()
                .map {
                    it.first { it.isActive }.id
                },
            etnaWalletsRepository
                .getActiveWalletObservable()
                .take(1)
                .singleOrError()
                .map {
                    it.blockchains.first { it.id == model.platform }.address
                },
            blockchainNetworksRepository
                .getPlatformsObservable()
                .take(1)
                .singleOrError()
                .map {
                    it.first { it.id == model.platform }.getWalletPath()
                })
            { walletId, walletAddress, hdWalletPath ->
                Triple(walletId, walletAddress, hdWalletPath)
            }
            .flatMapCompletable {

            val walletId = it.first
            val walletAddress = it.second
            val hdWalletPath = it.third

            val prepareTransactionRequest = model.getPrepareTransactionRequest(walletAddress)

            etnaWSSAPI
                .prepareTransaction(prepareTransactionRequest)
                .flatMapCompletable { unsignedTransaction ->
                    try {
                        val tosign = unsignedTransaction.get("tosign").asJsonArray.map { it.asString }
                        signTransaction(tosign, walletId, hdWalletPath)
                            .flatMapCompletable { signResult ->
                                unsignedTransaction.addProperty("platform", model.platform)

                                val signatures = JsonArray()
                                val pubkeys = JsonArray()
                                signResult.forEach {
                                    signatures.add(it.signature.toJsonObject())
                                    pubkeys.add(it.publicKey)
                                }

                                unsignedTransaction.add("signatures", signatures)
                                unsignedTransaction.add("pubkeys", pubkeys)

                                sendTransaction(unsignedTransaction)
                            }
                    } catch (e: Exception) {
                        Completable.error(PrepareTransactionError.UnableToPrepare)
                    }
                }
        }
    }

    private fun signTransaction(toSign: List<String>, walletId: String, hdWalletPath: IntArray): Single<List<SignResult>> {
        return Single.zip(
            toSign.map {
                cryptoWalletCredentialsRepository
                    .signTransaction(
                        walletId,
                        hdWalletPath,
                        it
                    )
            }
        ) {
            it.mapNotNull { it as? SignResult }
        }
    }

    private fun sendTransaction(signedTransaction: JsonObject): Completable {
        return etnaWSSAPI.sendTransaction(signedTransaction)
            .flatMapCompletable { Completable.complete() }
    }
}