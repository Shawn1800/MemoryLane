package com.ghostbug.memorylane.features.gallery.presentat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.ghostbug.memorylane.features.gallery.presentation.GalleryEvent
import com.ghostbug.memorylane.features.gallery.presentation.GalleryState
import com.ghostbug.memorylane.features.gallery.presentation.GalleryViewModel
import io.github.jan.supabase.realtime.Column
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.component.getScopeId

@Composable
fun  GalleryScreen (
    galleryViewModel: GalleryViewModel = koinViewModel(),
    onBack: () -> Unit
)  {
    val  galleryState  by galleryViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        galleryViewModel.onEvent(GalleryEvent.GetAllMemories)
    }

    GalleryContent(
        state= galleryState,
        modifier = Modifier
    )
}

@Composable
fun  GalleryContent(
    state: GalleryState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 50.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text("Your Memories")
        Box(Modifier.fillMaxSize()
            ) {
                if (state.memories.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No images yet")
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(110.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(
                            items=  state.memories,
                            key = {it.id}
                        ){
                            AsyncImage(
                                model = it.imageUrl,
                                contentDescription = it.caption,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)

                            )
                        }
                    }
                }
            }
        }
    }


