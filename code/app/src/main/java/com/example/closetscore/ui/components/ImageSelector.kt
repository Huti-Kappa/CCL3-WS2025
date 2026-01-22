package com.example.closetscore.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.toString


@Composable
fun AddImage(
    photoUri: String,
    onUriChange: (String) -> Unit
) {
    var showImageSelector by remember { mutableStateOf(false) }

    // Styling Variablen
    val shape = RoundedCornerShape(16.dp)
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    val borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp) // Feste Höhe macht es zu einem klaren UI-Block
            .padding(4.dp)
            .clip(shape)
            .background(backgroundColor)
            .clickable { showImageSelector = true }, // Klickbar auf der ganzen Fläche
        contentAlignment = Alignment.Center
    ) {
        if (photoUri.isEmpty()) {
            // --- ZUSTAND: LEER (PLACEHOLDER) ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CameraAlt,
                    contentDescription = "Add Photo",
                    modifier = Modifier.size(64.dp), // Etwas dezenter als 128dp
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Add Photo",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Tap to Choose",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // --- ZUSTAND: BILD VORHANDEN ---
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = "Selected photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dunkler Verlauf unten, damit man Icons besser sieht (optional)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(androidx.compose.ui.graphics.Color.Transparent, androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f)),
                                startY = 300f
                            )
                        )
                )

                // Löschen Button (Oben Rechts)
                IconButton(
                    onClick = { onUriChange("") },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Remove photo",
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                // Bearbeiten Hinweis (Unten Rechts) - Das macht es "obvious"
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        if (showImageSelector) {
            AlertDialog(
                onDismissRequest = { showImageSelector = false },
                title = { Text("Foto hinzufügen") },
                text = {
                    ImageSelector(
                        onImageSelected = { uri ->
                            onUriChange(uri)
                            showImageSelector = false
                        }
                    )
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showImageSelector = false }) {
                        Text("Abbrechen")
                    }
                }
            )
        }
    }
}

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
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null)
            Spacer(Modifier.padding(4.dp))
            Text("Take Photo")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
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

fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName,
        ".jpg",
        externalCacheDir
    )
}

fun Context.copyUriToAppStorage(sourceUri: Uri): File {
    val newFile = createImageFile()
    val inputStream = contentResolver.openInputStream(sourceUri)
    val originalBitmap = BitmapFactory.decodeStream(inputStream)
    inputStream?.close()

    FileOutputStream(newFile).use { outputStream ->
        originalBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
    }

    return newFile
}