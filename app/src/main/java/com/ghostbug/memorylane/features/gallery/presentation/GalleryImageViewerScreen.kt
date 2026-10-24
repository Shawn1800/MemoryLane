package com.ghostbug.memorylane.features.gallery.presentation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.ghostbug.memorylane.features.capture.domain.model.Memories

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun GalleryImageViewerScreen(
    id: String,
    memory: Memories,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackPressed: () -> Unit,
) {
    val imageShape = RoundedCornerShape(28.dp)
    val captionShape = RoundedCornerShape(20.dp)

    val boundsTransform = remember {
        BoundsTransform { _, _ -> tween(300) }
    }

    with(sharedTransitionScope) {
        with(animatedVisibilityScope) {
            val fadeEnter = fadeIn(tween(250))
            val fadeExit = fadeOut(tween(200))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onBackPressed,
                    ),
            ) {
                // Backdrop overlay
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .animateEnterExit(enter = fadeEnter, exit = fadeExit)
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest),
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    FilledTonalIconButton(
                        onClick = onBackPressed,
                        modifier = Modifier.animateEnterExit(enter = fadeEnter, exit = fadeExit),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(memory.imageUrl)
                            .memoryCacheKey(memory.imagePath)
                            .diskCacheKey(memory.imagePath)
                            .crossfade(true)
                            .build(),
                        contentDescription = memory.caption,
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(3f / 4f)
                                    .background(MaterialTheme.colorScheme.surfaceContainer),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        error = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(3f / 4f)
                                    .background(MaterialTheme.colorScheme.errorContainer),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "Couldn't load image",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                )
                            }
                        },
                        modifier = Modifier
                            .sharedElement(
                                sharedContentState = rememberSharedContentState(key = "image-$id"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = boundsTransform,
                                clipInOverlayDuringTransition = OverlayClip(imageShape),
                            )
                            .fillMaxWidth()
                            .clip(imageShape),
                    )

                    // Place Badge
                    if (memory.place.isNotBlank()) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            modifier = Modifier.animateEnterExit(enter = fadeEnter, exit = fadeExit)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Place,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = memory.place,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Caption Card
                    if (memory.caption.isNotBlank()) {
                        Surface(
                            shape = captionShape,
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateEnterExit(enter = fadeEnter, exit = fadeExit),
                        ) {
                            Text(
                                text = memory.caption,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .padding(20.dp)
                                    .sharedElement(
                                        sharedContentState = rememberSharedContentState(key = "text-$id"),
                                        animatedVisibilityScope = animatedVisibilityScope,
                                        boundsTransform = boundsTransform,
                                    )
                                    .fillMaxWidth(),
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}