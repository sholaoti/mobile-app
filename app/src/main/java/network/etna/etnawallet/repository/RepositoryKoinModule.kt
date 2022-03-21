package network.etna.etnawallet.repository

import network.etna.etnawallet.repository.auth0.Auth0Repository
import network.etna.etnawallet.repository.auth0.Auth0RepositoryImpl
import network.etna.etnawallet.repository.biometry.BiometryRepository
import network.etna.etnawallet.repository.biometry.BiometryRepositoryImpl
import network.etna.etnawallet.repository.blockchainnetworks.BlockchainNetworksRepository
import network.etna.etnawallet.repository.blockchainnetworks.BlockchainNetworksRepositoryImpl
import network.etna.etnawallet.repository.coinrate.CoinRateRepository
import network.etna.etnawallet.repository.coinrate.CoinRateRepositoryImpl
import network.etna.etnawallet.repository.erc721.ERC721InfoRepository
import network.etna.etnawallet.repository.erc721.ERC721InfoRepositoryImpl
import network.etna.etnawallet.repository.loadingIndicatorService.LoadingIndicatorService
import network.etna.etnawallet.repository.loadingIndicatorService.LoadingIndicatorServiceImpl
import network.etna.etnawallet.repository.mnemonic.MnemonicRepository
import network.etna.etnawallet.repository.mnemonic.MnemonicRepositoryImpl
import network.etna.etnawallet.repository.network.NetworkRepository
import network.etna.etnawallet.repository.network.NetworkRepositoryImpl
import network.etna.etnawallet.repository.pincode.PinCodeRepository
import network.etna.etnawallet.repository.pincode.PinCodeRepositoryImpl
import network.etna.etnawallet.repository.refresh.RefreshRepository
import network.etna.etnawallet.repository.refresh.RefreshRepositoryImpl
import network.etna.etnawallet.repository.storage.DataStorage
import network.etna.etnawallet.repository.storage.SecureDataStorageImpl
import network.etna.etnawallet.repository.wallets.cryptowallet.CryptoWalletsRepository
import network.etna.etnawallet.repository.wallets.cryptowallet.CryptoWalletsRepositoryImpl
import network.etna.etnawallet.repository.wallets.cryptowalletcredentials.CryptoWalletCredentialsRepository
import network.etna.etnawallet.repository.wallets.cryptowalletcredentials.CryptoWalletCredentialsRepositoryImpl
import network.etna.etnawallet.repository.wallets.etnawallet.EtnaWalletsRepository
import network.etna.etnawallet.repository.wallets.etnawallet.EtnaWalletsRepositoryImpl
import network.etna.etnawallet.repository.wallets.walletbalances.WalletBalancesRepository
import network.etna.etnawallet.repository.wallets.walletbalances.WalletBalancesRepositoryImpl
import network.etna.etnawallet.repository.wallets.walletsend.WalletSendRepository
import network.etna.etnawallet.repository.wallets.walletsend.WalletSendRepositoryImpl
import network.etna.etnawallet.repository.warningservice.WarningService
import network.etna.etnawallet.repository.warningservice.WarningServiceImpl
import network.etna.etnawallet.sdk.core.AccessTokenProvider
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {

    single<DataStorage> { SecureDataStorageImpl(get()) }

    single<LoadingIndicatorService> {
        LoadingIndicatorServiceImpl()
    }

    single { Auth0RepositoryImpl() }
        .bind(Auth0Repository::class)
        .bind(AccessTokenProvider::class)

    single<BiometryRepository> {
        BiometryRepositoryImpl(get())
    }

    single<PinCodeRepository> {
        PinCodeRepositoryImpl()
    }

    single<CoinRateRepository> {
        CoinRateRepositoryImpl()
    }

    single<BlockchainNetworksRepository> {
        BlockchainNetworksRepositoryImpl()
    }

    single<CryptoWalletCredentialsRepository> {
        CryptoWalletCredentialsRepositoryImpl()
    }

    single<CryptoWalletsRepository> {
        CryptoWalletsRepositoryImpl()
    }

    single<WalletBalancesRepository> {
        WalletBalancesRepositoryImpl()
    }

    single<EtnaWalletsRepository> {
        EtnaWalletsRepositoryImpl()
    }

    single<WarningService> {
        WarningServiceImpl(get())
    }

    single<MnemonicRepository> {
        MnemonicRepositoryImpl()
    }

    single<NetworkRepository>(null, true) {
        NetworkRepositoryImpl(get())
    }

    single<WalletSendRepository> {
        WalletSendRepositoryImpl()
    }

    single<ERC721InfoRepository> {
        ERC721InfoRepositoryImpl()
    }

    single<RefreshRepository> {
        RefreshRepositoryImpl()
    }
}