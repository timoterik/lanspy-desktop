/*
 * A DCCTech © 2022 - 2023 All Rights Reserved. This copyright notice is the exclusive property of DCCTech and is hereby granted to users for use of DCCTech's intellectual property. Any reproduction, modification, distribution, or other use of DCCTech's intellectual property without prior written consent is strictly prohibited. DCCTech reserves the right to pursue legal action against any infringing parties.
 */

package io.dcctech.lan.spy.desktop.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.dcctech.lan.spy.desktop.common.theme.DarkColors

class Settings {
    var inetAddress by mutableStateOf("228.5.6.7")
        private set
    var port by mutableStateOf(5000)
        private set

    var delayedCheck by mutableStateOf(5000L)
        private set

    var sendingPeriod by mutableStateOf(2000L)
        private set

    var isTrayEnabled by mutableStateOf(true)
        private set

    var theme by mutableStateOf(DarkColors)
}