package com.ryan.dicodingstorycompose.presentation.camera

import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OnImageSavedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.ryan.dicodingstorycompose.common.components.CameraPreview
import com.ryan.dicodingstorycompose.common.createFile
import com.ryan.dicodingstorycompose.common.uriToFile
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun CameraScreen(
    onNavigateBackWithResult: (String) -> Unit,
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        }
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                val imageFile = uriToFile(uri, context)
                Log.d("RyanTag", "Image selected: ${imageFile.absolutePath}")
                lifecycleOwner.lifecycleScope.launch {
                    val compressedImageFile = Compressor.compress(context, imageFile)
                    Log.d("RyanTag", "Compressed file: ${compressedImageFile.absolutePath}")
                    imageFile.delete()
                    onNavigateBackWithResult(compressedImageFile.absolutePath)
                }
            }
        }
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        //TODO set the correct camera aspect ratio
        CameraPreview(
            controller = controller,
            modifier = Modifier
                .aspectRatio(1f/1f)
                .align(Alignment.Center)
        )

        IconButton(
            onClick = {
                controller.cameraSelector =
                    if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    } else CameraSelector.DEFAULT_BACK_CAMERA
            },
            modifier = Modifier.offset(16.dp, 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Cameraswitch,
                contentDescription = "Switch camera"
            )
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(
                onClick = {
                    singlePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Photo,
                    contentDescription = "Open gallery",
                    modifier = Modifier.size(50.dp)
                )
            }
            IconButton(
                onClick = {
                    takePhoto(
                        context = context,
                        lifecycleOwner = lifecycleOwner,
                        controller = controller,
                        onImageCaptured = { file ->
                            onNavigateBackWithResult(file.absolutePath)
                        }
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Take photo",
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }
}

private fun takePhoto(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    controller: LifecycleCameraController,
    onImageCaptured: (File) -> Unit,
) {
    val photoFile = createFile(context)
    controller.takePicture(
        ImageCapture.OutputFileOptions.Builder(photoFile).build(),
        ContextCompat.getMainExecutor(context),
        object : OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                lifecycleOwner.lifecycleScope.launch {
                    val compressedImageFile = Compressor.compress(context, photoFile)
                    Log.d("RyanTag", "Compressed file: ${compressedImageFile.absolutePath}")
                    photoFile.delete()
                    onImageCaptured(compressedImageFile)
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("RyanTag", "Couldn't take photo: ", exception)
            }
        }
    )
}