package com.ghostbug.memorylane.features.gallery.presentat


import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

import com.ghostbug.memorylane.features.gallery.presentation.GalleryState
import com.ghostbug.memorylane.features.gallery.presentation.GalleryViewModel

import org.koin.compose.viewmodel.koinViewModel


@Composable
fun GalleryScreen(
    galleryViewModel: GalleryViewModel = koinViewModel(),
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onImageClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val galleryState by galleryViewModel.state.collectAsStateWithLifecycle()

    GalleryContent(
        state = galleryState, modifier = Modifier, onBack = onBack ,
        sharedTransitionScope = sharedTransitionScope,animatedVisibilityScope = animatedVisibilityScope,
        onImageClick = onImageClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryContent(
    onBack: () -> Unit,
    state: GalleryState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onImageClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Memories", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = { BackArrowIcon(onBack = onBack) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your Memories",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Box(modifier = Modifier.fillMaxSize()) {
            if (state.loading) {
                // 1. Show loading spinner FIRST when loading is true!
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (!state.error.isNullOrBlank()) {
                // 2. Show error if fetching failed
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (state.memories.isEmpty()) {
                // 3. Show "No images yet" ONLY when loading finished AND memories is empty
                Text(
                    text = "No images yet",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(110.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(
                        items = state.memories,
                        key = { it.id },
                    ) {
                        Surface(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clickable { onImageClick(it.id) },
                            shape =  RoundedCornerShape(15.dp)
                        )
                       {
                        with(sharedTransitionScope) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(it.imageUrl)
                                    .memoryCacheKey(it.imagePath) // Uses constant "userId/photo.jpg" as cache key!
                                    .crossfade(true)
                                    .build(),
                                contentDescription = it.caption,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.aspectRatio(1f)
                                    .sharedElement(
                                        sharedTransitionScope.rememberSharedContentState(key= "image-${it.id}"),
                                        animatedVisibilityScope = animatedVisibilityScope
                                    ).size(100.dp)
                            )
                        }
                    }}
                }
            }
        }
    }
}}

@Composable
fun BackArrowIcon(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    FilledTonalIconButton(onClick = onBack, enabled = enabled, modifier = modifier) {
        Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "Discard photo")
    }
}




