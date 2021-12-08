package com.hkamran.schedules

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.Role

@Composable
fun Spacer(modifier: Modifier) {
    Layout({}, modifier) { _, constraints ->
        with(constraints) {
            val width = if (hasFixedWidth) maxWidth else 0
            val height = if (hasFixedHeight) maxHeight else 0
            layout(width, height) {}
        }
    }
}