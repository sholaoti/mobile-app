package network.etna.etnawallet.ui.addwallet.importsinglewallet

import network.etna.etnawallet.R

enum class ImportVariant(
    val nameResourceId: Int,
    val iconResourceId: Int,
    val fragmentId: Int,
    var isActive: Boolean) {
    RECOVERY_PHRASE(
        R.string.import_variant_recovery_phrase,
        R.drawable.import_variant_seed_selector,
        R.id.ImportSingleWalletSeedFragment,
        true
    ),
//    KEYSTORE_JSON(
//        R.string.import_variant_keystore_json,
//        R.drawable.import_variant_keystore_selector,
//        R.id.ImportSingleWalletSeedFragment,
//        false
//    ),
    PRIVATE_KEY(
        R.string.import_variant_private_key,
        R.drawable.import_variant_keystore_selector,
        R.id.ImportSingleWalletPrivateKeyFragment,
        false
    ),
    ADDRESS(
        R.string.import_variant_address,
        R.drawable.import_variant_address_selector,
        R.id.ImportSingleWalletAddressFragment,
        false
    )
}
