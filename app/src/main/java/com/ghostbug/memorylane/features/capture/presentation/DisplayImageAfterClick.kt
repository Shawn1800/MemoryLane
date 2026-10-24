package com.ghostbug.memorylane.features.capture.presentation

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.mapbox.maps.extension.style.expressions.dsl.generated.image

private const val CAPTION_MAX = 140

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayImageAfterClickScreen(
    onBack: () -> Unit,
    imageUri: Uri?,
    caption: String,
    onCaptionChanged: (String) -> Unit,
    onUpload: () -> Unit,
    modifier: Modifier = Modifier,
    isUploading: Boolean = false,
    placeName: String? = null,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("New memory", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = { CancelIcon(onBack = onBack, enabled = !isUploading,imageUri = imageUri) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Box(
                Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                UploadButton(onUpload = onUpload, isUploading = isUploading)
            }
        }
    ) { padding ->
        DisplayImageAfterClickContent(
            imageUri = imageUri,
            caption = caption,
            onCaptionChanged = onCaptionChanged,
            placeName = placeName,
            isUploading = isUploading,
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
        )
    }
}

@Composable
fun DisplayImageAfterClickContent(
    imageUri: Uri?,
    caption: String,
    onCaptionChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeName: String? = null,
    isUploading: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        ClickedImage(imageUri = imageUri, placeName = placeName)

        CaptionField(
            caption = caption,
            onCaptionChange = onCaptionChanged,
            enabled = !isUploading,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun ClickedImage(
    modifier: Modifier = Modifier,
    imageUri: Uri? = null,
    placeName: String? = null,
) {
    val visibleState = remember { MutableTransitionState(false).apply { targetState = true } }

    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn() + scaleIn(initialScale = 0.9f),
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(4f / 5f)
                .clip(RoundedCornerShape(36.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Captured image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            if (!placeName.isNullOrBlank()) {
                PlaceChip(
                    name = placeName,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun PlaceChip(name: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.85f),
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Row(
            modifier = Modifier.padding(start = 10.dp, end = 14.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                Icons.Filled.Place,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Text(name, style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun CaptionField(
    caption: String,
    onCaptionChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    TextField(
        value = caption,
        onValueChange = { if (it.length <= CAPTION_MAX) onCaptionChange(it) },
        enabled = enabled,
        modifier = modifier,
        label = { Text("Add a caption") },
        placeholder = { Text("What made this moment special?") },
        supportingText = {
            Text(
                "${caption.length}/$CAPTION_MAX",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
        },
        minLines = 2,
        maxLines = 4,
        shape = RoundedCornerShape(28.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Default
        )
    )
}

@Composable
fun UploadButton(
    onUpload: () -> Unit,
    modifier: Modifier = Modifier,
    isUploading: Boolean = false,
) {
    Button(
        onClick = onUpload,
        enabled = !isUploading,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
    ) {
        if (isUploading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Icon(
                imageVector = Icons.Filled.CloudUpload,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
            Text("Upload memory", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun CancelIcon(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    imageUri: Uri?
) {
    FilledTonalIconButton(onClick = onBack , enabled = enabled, modifier = modifier) {
        Icon(Icons.Filled.Close, contentDescription = "Discard photo")
    }
}

