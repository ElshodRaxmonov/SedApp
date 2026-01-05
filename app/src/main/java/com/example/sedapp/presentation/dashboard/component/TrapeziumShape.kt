package com.example.sedapp.presentation.dashboard.component

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class TrapeziumShape(
    private val topPadding: Float = 40f,
    private val cornerRadius: Float = 24f
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {

        val r = cornerRadius
        val path = Path().apply {

            // Top left
            moveTo(topPadding + r, 0f)

            // Top edge
            lineTo(size.width - topPadding - r, 0f)
            quadraticBezierTo(
                size.width - topPadding,
                0f,
                size.width - topPadding,
                r
            )

            // Right edge
            lineTo(size.width - r, size.height - r)
            quadraticBezierTo(
                size.width,
                size.height,
                size.width - r,
                size.height
            )

            // Bottom edge
            lineTo(r, size.height)
            quadraticBezierTo(
                0f,
                size.height,
                0f,
                size.height - r
            )

            // Left edge
            lineTo(topPadding, r)
            quadraticBezierTo(
                topPadding,
                0f,
                topPadding + r,
                0f
            )

            close()
        }

        return Outline.Generic(path)
    }
}

