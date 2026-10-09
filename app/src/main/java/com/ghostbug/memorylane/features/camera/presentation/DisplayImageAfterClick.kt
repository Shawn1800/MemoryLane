package com.ghostbug.memorylane.features.camera.presentation

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage




@Composable
fun DisplayImageAfterClick(
    onBack:()->Unit,
    imageUri: Uri?,
    modifier: Modifier,
    onUpload:()->Unit
) {
    DisplayImageAfterClickContent(imageUri = imageUri, modifier = modifier,onUpload = onUpload)

}

@Composable
fun DisplayImageAfterClickContent(
    imageUri: Uri?,
    modifier: Modifier,
    onUpload: () -> Unit
) {
    LazyColumn(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            ClickedImage(imageUri = imageUri, modifier = modifier)
        }
        item {
            UploadButton(onUpload = onUpload,modifier = modifier)
        }
    }
}

@Composable
fun ClickedImage(
    modifier: Modifier = Modifier,
    imageUri: Uri? = null
) {
    if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Captured image",
                modifier = Modifier.size(250.dp)
            )
        }
    }

@Composable
fun UploadButton(
    onUpload: () -> Unit,
    modifier: Modifier = Modifier,

) {
    Button(onClick = { onUpload() }) {
        Text("Upload")
    }

}

@Composable
fun CancelIcon(
    onBack: () -> Unit,
    modifier: Modifier
) {


}
