package com.ghostbug.memorylane.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Brand Color Palette
object MemoryThemeColors {
    val ForestGreen = Color(0xFF1B5E20)
    val PineAccent = Color(0xFF2E7D32)
    val MintSage = Color(0xFFA9D6B0)
    val MintLight = Color(0xFFE8F5E9)
    val WarmPeach = Color(0xFFF3A989)
    val PeachLight = Color(0xFFFFEBE3)
    val SunGold = Color(0xFFF0C14E)
    val Charcoal = Color(0xFF14171A)
    val CardBg = Color(0xFFFFFFFF)
    val OatBg = Color(0xFFFAF7F2)
    val BorderDark = Color(0xFF1E232A)
    val BorderLight = Color(0xFFE2DDD5)
    val TextMuted = Color(0xFF6B7280)
}

/**
 * Tactile spring bouncy click modifier for natural micro-interactions.
 */
fun Modifier.bouncyClick(
    scaleDown: Float = 0.94f,
    onClick: () -> Unit
) = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        label = "bouncyScale"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                },
                onTap = { onClick() }
            )
        }
}

/**
 * Production-ready primary tactile button with shadow elevation, optional icon, and loading state.
 */
@Composable
fun MemoryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    containerColor: Color = MemoryThemeColors.ForestGreen,
    contentColor: Color = Color.White,
    shape: Shape = RoundedCornerShape(16.dp),
    shadowElevation: Dp = 4.dp
) {
    Surface(
        onClick = { if (!isLoading && enabled) onClick() },
        enabled = enabled && !isLoading,
        shape = shape,
        color = if (enabled) containerColor else containerColor.copy(alpha = 0.5f),
        shadowElevation = if (enabled) shadowElevation else 0.dp,
        modifier = modifier
            .bouncyClick(scaleDown = 0.95f) { if (!isLoading && enabled) onClick() }
            .height(52.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = contentColor,
                    strokeWidth = 2.5.dp
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = text,
                        color = contentColor,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

/**
 * Tactile pill badge for locations, tags, and date chips.
 */
@Composable
fun MemoryPillBadge(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    backgroundColor: Color = MemoryThemeColors.MintLight,
    textColor: Color = MemoryThemeColors.ForestGreen,
    borderColor: Color? = MemoryThemeColors.MintSage
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .then(
                if (borderColor != null) Modifier.border(1.dp, borderColor, CircleShape)
                else Modifier
            )
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Tactile Neo-Card with clean border, smooth background, and rounded corners.
 */
@Composable
fun NeoCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = MemoryThemeColors.CardBg,
    borderColor: Color = MemoryThemeColors.BorderLight,
    borderWidth: Dp = 1.dp,
    elevation: Dp = 2.dp,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .border(borderWidth, borderColor, shape),
        shape = shape,
        color = backgroundColor,
        shadowElevation = elevation
    ) {
        content()
    }
}
