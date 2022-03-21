package network.etna.etnawallet.sdk.api.etna.wssapi

import network.etna.etnawallet.sdk.core.APICore
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class EtnaWSSAPI: KoinComponent {
    internal val apiCore: APICore by inject()
}
