package network.etna.etnawallet

import network.etna.etnawallet.databinding.FragmentNotificationBinding
import network.etna.etnawallet.ui.base.BaseFragment

class NotificationFragment :
    BaseFragment<FragmentNotificationBinding>(
        FragmentNotificationBinding::inflate,
        R.string.tabbar_notification
    )