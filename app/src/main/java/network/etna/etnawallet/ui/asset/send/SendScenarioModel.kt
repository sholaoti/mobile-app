package network.etna.etnawallet.ui.asset.send

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import network.etna.etnawallet.repository.wallets.etnawallet.AssetIdHolder
import org.koin.core.component.KoinComponent
import java.util.*

class SendScenarioModel: KoinComponent {

    private val assetSubject: BehaviorSubject<Optional<AssetIdHolder>> =
        BehaviorSubject.createDefault(Optional.empty())

    private val recipientAddressSubject: BehaviorSubject<Optional<String>> =
        BehaviorSubject.createDefault(Optional.of(""))

    fun getPlatformObservable(): Observable<String> {
        return getAssetIdObservable()
            .map { it.platform }
    }

    fun getRecipientAddressObservable(): Observable<Optional<String>> {
        return recipientAddressSubject
    }

    fun updateRecipientAddress(address: String) {
        recipientAddressSubject.onNext(Optional.of(address))
    }

    fun getAssetIdObservable(): Observable<AssetIdHolder> {
        return assetSubject
            .filter { it.isPresent }
            .map { it.get() }
    }

    fun getAssetId(): AssetIdHolder {
        return assetSubject.value!!.get()
    }

    fun updateAsset(assetId: AssetIdHolder) {
        assetSubject.onNext(Optional.of(assetId))
    }

    fun getPlatform(): String {
        return assetSubject.value!!.get().platform
    }

    fun getRecipientAddress(): String {
        return recipientAddressSubject.value!!.get()
    }
}
