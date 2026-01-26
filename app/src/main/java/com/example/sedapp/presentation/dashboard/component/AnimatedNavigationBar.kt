package com.example.sedapp.presentation.dashboard.component


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffset
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.sedapp.R
import com.example.sedapp.core.ui.theme.Charcoal
import com.example.sedapp.core.ui.theme.SoftGold
import com.example.sedapp.core.ui.theme.WarmWhite


// Source - https://stackoverflow.com/a
// Posted by Jan Itor
// Retrieved 2026-01-11, License - CC BY-SA 4.0

data class ButtonData(val text: String, val icon: Int, val badgeCount: Int = 0)


@Composable
fun AnimatedNavigationBar(
    buttons: List<ButtonData>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
    barColor: Color,
    circleColor: Color,
    selectedColor: Color,
    unselectedColor: Color,
    modifier: Modifier = Modifier
) {
    val circleRadius = 26.dp
    val cornerRadius = 32.dp

    var barSize by remember { mutableStateOf(IntSize(0, 0)) }
    val offsetStep = remember(barSize) {
        if (barSize.width == 0) 0f else barSize.width.toFloat() / (buttons.size * 2)
    }
    val offset = remember(selectedItem, offsetStep) {
        offsetStep + selectedItem * 2 * offsetStep
    }
    val circleRadiusPx = LocalDensity.current.run { circleRadius.toPx().toInt() }
    val offsetTransition = updateTransition(offset, "offset transition")

    // Optimization: Increased stiffness to Medium (1500f) for a much snappier feel
    val animation = spring<Float>(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium)

    val cutoutOffsetState = offsetTransition.animateFloat(
        transitionSpec = {
            if (this.initialState == 0f) snap() else animation
        },
        label = "cutout offset"
    ) { it }

    val circleOffset by offsetTransition.animateIntOffset(
        transitionSpec = {
            if (this.initialState == 0f) snap() else spring(animation.dampingRatio, animation.stiffness)
        },
        label = "circle offset"
    ) {
        IntOffset(it.toInt() - circleRadiusPx, -circleRadiusPx)
    }

    Box(modifier = modifier) {
        Circle(
            modifier = Modifier
                .offset { circleOffset }
                .zIndex(1f),
            color = circleColor,
            radius = circleRadius,
            button = buttons[selectedItem],
            iconColor = selectedColor,
        )
        Row(
            modifier = Modifier
                .onPlaced { barSize = it.size }
                .graphicsLayer {
                    // Optimization: Read animated state inside graphicsLayer lambda to avoid recomposing the whole Row
                    val currentOffset = cutoutOffsetState.value
                    if (currentOffset > 0) {
                        this.shape = BarShape(currentOffset, circleRadius, cornerRadius)
                        this.clip = true
                        this.shadowElevation = 22.dp.toPx()
                    }
                }
                .fillMaxWidth()
                .background(barColor)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            buttons.forEachIndexed { index, button ->
                val isSelected = index == selectedItem
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onItemSelected(index) },
                    icon = {
                        val iconAlpha by animateFloatAsState(
                            targetValue = if (isSelected) 0f else 1f,
                            label = "Navbar item icon"
                        )
                        BadgedBox(
                            badge = {
                                if (button.badgeCount > 0) {
                                    Badge(containerColor = SoftGold, contentColor = Charcoal) {
                                        Text(button.badgeCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = button.icon),
                                contentDescription = button.text,
                                modifier = Modifier
                                    .alpha(iconAlpha)
                                    .size(24.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = button.text,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors().copy(
                        selectedIconColor = selectedColor,
                        selectedTextColor = selectedColor,
                        unselectedIconColor = unselectedColor,
                        unselectedTextColor = unselectedColor,
                        selectedIndicatorColor = Color.Transparent,
                    )
                )
            }
        }
    }
}

private class BarShape(
    private val offset: Float,
    private val circleRadius: Dp,
    private val cornerRadius: Dp,
    private val circleGap: Dp = 5.dp,
) : androidx.compose.ui.graphics.Shape {

    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: Density
    ): Outline = Outline.Generic(getPath(size, density))

    private fun getPath(size: androidx.compose.ui.geometry.Size, density: Density): Path {
        val cutoutCenterX = offset
        val cutoutRadius = density.run { (circleRadius + circleGap).toPx() }
        val cornerRadiusPx = density.run { cornerRadius.toPx() }
        val cornerDiameter = cornerRadiusPx * 2

        return Path().apply {
            val cutoutEdgeOffset = cutoutRadius * 1.5f
            val cutoutLeftX = cutoutCenterX - cutoutEdgeOffset
            val cutoutRightX = cutoutCenterX + cutoutEdgeOffset

            moveTo(0f, size.height - cornerRadiusPx)
            arcTo(
                rect = Rect(0f, size.height - cornerDiameter, cornerDiameter, size.height),
                startAngleDegrees = 180f,
                sweepAngleDegrees = -90f,
                forceMoveTo = false
            )
            lineTo(size.width - cornerRadiusPx, size.height)
            arcTo(
                rect = Rect(size.width - cornerDiameter, size.height - cornerDiameter, size.width, size.height),
                startAngleDegrees = 90f,
                sweepAngleDegrees = -90f,
                forceMoveTo = false
            )
            lineTo(size.width, cornerRadiusPx)

            val realRightCornerDiameter = if (cutoutRightX <= size.width - cornerRadiusPx) cornerDiameter else (size.width - cutoutRightX) * 2
            arcTo(
                rect = Rect(size.width - realRightCornerDiameter, 0f, size.width, realRightCornerDiameter),
                startAngleDegrees = 0f,
                sweepAngleDegrees = -90f,
                forceMoveTo = false
            )
            lineTo(cutoutRightX, 0f)
            cubicTo(cutoutCenterX + cutoutRadius, 0f, cutoutCenterX + cutoutRadius, cutoutRadius, cutoutCenterX, cutoutRadius)
            cubicTo(cutoutCenterX - cutoutRadius, cutoutRadius, cutoutCenterX - cutoutRadius, 0f, cutoutLeftX, 0f)

            val realLeftCornerDiameter = if (cutoutLeftX >= cornerRadiusPx) cornerDiameter else cutoutLeftX * 2
            arcTo(
                rect = Rect(0f, 0f, realLeftCornerDiameter, realLeftCornerDiameter),
                startAngleDegrees = -90.0f,
                sweepAngleDegrees = -90.0f,
                forceMoveTo = false
            )
            close()
        }
    }
}

@Composable
private fun Circle(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    radius: Dp,
    button: ButtonData,
    iconColor: Color,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(radius * 2)
            .clip(CircleShape)
            .background(color),
    ) {
        AnimatedContent(targetState = button.icon, label = "Bottom bar circle icon") { targetIcon ->
            BadgedBox(
                badge = {
                    if (button.badgeCount > 0) {
                        Badge(containerColor = WarmWhite, contentColor = Charcoal) {
                            Text(button.badgeCount.toString())
                        }
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = targetIcon),
                    contentDescription = button.text,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun previewbar() {
    val buttons = listOf(
        ButtonData(text = "Home", icon = R.drawable.home_icon),
        ButtonData(text = "Bag", icon = R.drawable.bag, badgeCount = 3),
        ButtonData(text = "Orders", icon = R.drawable.orders),
        ButtonData(text = "Profile", icon = R.drawable.frame),
    )
    var selectedItem by remember { mutableIntStateOf(0) }

    AnimatedNavigationBar(
        buttons = buttons,
        selectedItem = selectedItem,
        onItemSelected = { selectedItem = it },
        barColor = Color.White,
        circleColor = Color.White,
        selectedColor = Color.Blue,
        unselectedColor = Color.Gray,
    )
}
