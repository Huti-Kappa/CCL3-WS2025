package com.example.closetscore.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.toString

@Composable
fun ImageSelector(
    onImageSelected: (String) -> Unit
) {
    val context = LocalContext.current
    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && currentPhotoUri != null) {
            onImageSelected(currentPhotoUri.toString())
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedFile = context.copyUriToAppStorage(uri)
            val permanentUri = Uri.fromFile(savedFile)
            onImageSelected(permanentUri.toString())
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val file = context.createImageFile()
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                currentPhotoUri = uri
                cameraLauncher.launch(uri)
            }
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null)
            Spacer(Modifier.padding(4.dp))
            Text("Take Photo")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BUTTON 2: Gallery
        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
            onClick = {
                galleryLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        ) {
            Icon(Icons.Default.PhotoLibrary, contentDescription = null)
            Spacer(Modifier.padding(4.dp))
            Text("Upload from Gallery")
        }
    }
}

// --- HELPER FUNCTIONS ---

fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName,
        ".jpg",
        externalCacheDir
    )
}

// This function takes a Gallery URI, opens it, compresses it slightly,
// and saves a copy in your app's private storage.
fun Context.copyUriToAppStorage(sourceUri: Uri): File {
    val newFile = createImageFile() // Reuse the helper above!

    val inputStream = contentResolver.openInputStream(sourceUri)
    val originalBitmap = BitmapFactory.decodeStream(inputStream)
    inputStream?.close()

    FileOutputStream(newFile).use { outputStream ->
        // Compress to JPEG with 80% quality (saves space, looks great)
        originalBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
    }

    return newFile
}
