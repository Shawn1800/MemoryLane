package com.ghostbug.memorylane.core.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey

@Composable
fun BottomNavigationBar(
    selectedKey: NavKey,
    onSelectKey: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .size(width = 200.dp, height = 150.dp)
            .padding(horizontal = 12.dp, vertical = 35.dp),
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
    modifier: Modifier = Modifier
) {
    Surface(
        shape = CircleShape,
        color = Color.White,
        shadowElevation = 12.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TOP_LEVEL_DESTINATIONS.forEach { (route, dest) ->
                val selected = selectedKey == route
                val label = labelFor(route)

                // Icon spring pop on selection
                val pop = remember { Animatable(1f) }
                LaunchedEffect(selected) {
                    if (selected) {
                        pop.snapTo(0.65f)
                        pop.animateTo(
                            1f,
                            spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = if (selected) Color(0xFF1B6E1B)
                    else Color.Transparent,
                    modifier = Modifier
                        .bouncyClickable(scaleDown = 0.88f) { onSelectKey(route) }
                        .clip(CircleShape)
                        .animateContentSize()
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = if (selected) 18.dp else 12.dp,
                            vertical = 12.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = dest.icon,
                            contentDescription = label,
                            tint = if (selected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .size(24.dp)
                                .graphicsLayer {
                                    scaleX = pop.value
                                    scaleY = pop.value
                                }
                        )
                        AnimatedVisibility(
                            visible = selected,
                            enter = expandHorizontally() + fadeIn(),
                            exit = shrinkHorizontally() + fadeOut()
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun labelFor(route: NavKey): String = when (route) {
    is Route.LocationScreen -> "Home"
    is Route.CameraScreen   -> "Camera"
    is Route.GalleryScreen   -> "Memories"
    is Route.ProfileScreen   -> "Profile"
    else                     -> ""
}


fun Modifier.bouncyClickable(
    scaleDown: Float = 0.88f,
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