/*
 * A DCCTech © 2022 - 2023 All Rights Reserved. This copyright notice is the exclusive property of DCCTech and
 * is hereby granted to users for use of DCCTech's intellectual property. Any reproduction, modification, distribution,
 * or other use of DCCTech's intellectual property without prior written consent is strictly prohibited.
 *
 */

package io.dcctech.lan.spy.desktop.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.dcctech.lan.spy.desktop.data.NetworkService


@Composable
fun NetworkList(list: List<NetworkService>) {
    Box {

        val state = rememberLazyListState()

        LazyColumn(
            Modifier.fillMaxWidth().border(BorderStroke(3.dp, Color.Black)), state
        ) {

            if (list.isEmpty()) {
                items(1) {
                    EmptyCard()
                }
            } else {
                items(list) { network ->
                    NetworkCard(network)
                    Divider(startIndent = 8.dp, thickness = 1.dp, color = Color.Black)
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }

        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = state
            )
        )
    }
}









