/*
 * A DCCTech © 2022 - 2023 All Rights Reserved. This copyright notice is the exclusive property of DCCTech and
 * is hereby granted to users for use of DCCTech's intellectual property. Any reproduction, modification, distribution,
 * or other use of DCCTech's intellectual property without prior written consent is strictly prohibited.
 *
 */

package io.dcctech.lan.spy.desktop.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.dcctech.lan.spy.desktop.common.R
import io.dcctech.lan.spy.desktop.data.Client
import io.dcctech.lan.spy.desktop.data.LogLevel
import io.dcctech.lan.spy.desktop.data.Ssdp.Companion.getColorByStatus
import io.dcctech.lan.spy.desktop.utils.log
import java.awt.Desktop
import java.net.URI

@Composable
fun ShowDevice(client: Client) {
    Box(
        modifier = Modifier.height(65.dp)
            .fillMaxWidth()
            .background(color = client.getColorByStatus()),
        contentAlignment = Alignment.CenterStart
    ) {
        DeviceCard(client) {
            val uri = URI.create("http:/${client.address}")
            try {
                val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
                if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(uri)
                }
            } catch (e: Exception) {
                val msg = R.couldNotOpenedInBrowser.replace("uri", "$uri")
                LogLevel.ERROR.log("$msg\n${e.printStackTrace()}")
            }
        }
    }
}