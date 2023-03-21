/*
 * A DCCTech © 2022 - 2023 All Rights Reserved. This copyright notice is the exclusive property of DCCTech and
 * is hereby granted to users for use of DCCTech's intellectual property. Any reproduction, modification, distribution,
 * or other use of DCCTech's intellectual property without prior written consent is strictly prohibited.
 *
 */

package io.dcctech.lan.spy.desktop.components

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

/**
 * Helper function that allows to use Composable functions that return value in non-composable scope
 */
@Suppress("UNCHECKED_CAST")
suspend fun <T> compose(content: @Composable () -> T): T {
    var result: Any? = Unit
    withContext(YieldFrameClock) {
        withRunningRecomposer { recomposer ->
            val composition = Composition(UnitApplier(), recomposer)
            composition.setContent {
                val density = Density(1f)
                val layoutDirection = LayoutDirection.Ltr
                CompositionLocalProvider(
                    LocalDensity provides density,
                    LocalLayoutDirection provides layoutDirection,
                ) {
                    result = content()
                }
            }
        }
    }
    return result as T
}

private object YieldFrameClock : MonotonicFrameClock {
    override suspend fun <R> withFrameNanos(
        onFrame: (frameTimeNanos: Long) -> R
    ): R {
        yield()
        return onFrame(System.nanoTime())
    }
}

private class UnitApplier : Applier<Unit> {
    override val current: Unit = Unit
    override fun down(node: Unit) = Unit
    override fun up() = Unit
    override fun insertTopDown(index: Int, instance: Unit) = Unit
    override fun insertBottomUp(index: Int, instance: Unit) = Unit
    override fun remove(index: Int, count: Int) = Unit
    override fun move(from: Int, to: Int, count: Int) = Unit
    override fun clear() = Unit
    override fun onEndChanges() = Unit
}