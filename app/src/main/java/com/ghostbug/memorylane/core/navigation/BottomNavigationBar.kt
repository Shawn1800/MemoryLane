package com.ghostbug.memorylane.core.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey

// Hoisted so they're allocated once, not on every recomposition.
private val BouncySpring = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessMedium
)
private val LabelEnter = expandHorizontally(expandFrom = Alignment.Start) +
        fadeIn(tween(durationMillis = 180, delayMillis = 60))
private val LabelExit = shrinkHorizontally(shrinkTowards = Alignment.Start) +
        fadeOut(tween(durationMillis = 90))

@Composable
fun BottomNavigationBar(
    selectedKey: NavKey,
    onSelectKey: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        StrideFloatingNav(
            selectedKey = selectedKey,
            onSelectKey = onSelectKey
        )
    }
}

@Composable
fun StrideFloatingNav(
    selectedKey: NavKey,
    onSelectKey: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.White,
    selectedColor: Color = Color(0xFF1B6E1B)
) {
    Surface(
        shape = CircleShape,
        color = containerColor,
        shadowElevation = 12.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TOP_LEVEL_DESTINATIONS.forEach { (route, dest) ->
                key(route) {
                    NavItem(
                        icon = dest.icon,
                        label = labelFor(route),
                        selected = selectedKey == route,
                        selectedColor = selectedColor,
                        onClick = { onSelectKey(route) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    // Kept as State (no `by`) and read inside graphicsLayer / drawBehind lambdas,
    // so these animations run in the draw phase without recomposing every frame.
    val pressScale = animateFloatAsState(
        targetValue = if (pressed) 0.88f else 1f,
        animationSpec = BouncySpring,
        label = "pressScale"
    )
    val iconScale = animateFloatAsState(
        targetValue = if (selected) 1.12f else 1f,
        animationSpec = BouncySpring,
        label = "iconScale"
    )
    val background = animateColorAsState(
        // Fade to the *same hue* at alpha 0 — animating to Color.Transparent
        // passes through a muddy dark tint.
        targetValue = if (selected) selectedColor else selectedColor.copy(alpha = 0f),
        label = "background"
    )
    val iconTint = animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "iconTint"
    )

    Row(
        modifier = Modifier
            .graphicsLayer {
                scaleX = pressScale.value
                scaleY = pressScale.value
            }
            .clip(CircleShape)
            .drawBehind { drawRect(background.value) }
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Tab,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = if (selected) null else label, // label is visible when selected
            tint = iconTint.value,
            modifier = Modifier
                .size(24.dp)
                .graphicsLayer {
                    scaleX = iconScale.value
                    scaleY = iconScale.value
                }
        )
        AnimatedVisibility(
            visible = selected,
            enter = LabelEnter,
            exit = LabelExit
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                // Spacing lives here, so unselected items have no leftover gap.
                modifier = Modifier.padding(start = 8.dp, end = 6.dp)
            )
        }
    }
}

private fun labelFor(route: NavKey): String = when (route) {
    is Route.LocationScreen -> "Home"
    is Route.CameraScreen -> "Camera"
    is Route.GalleryScreen -> "Memories"
    is Route.ProfileScreen -> "Profile"
    else -> ""
}