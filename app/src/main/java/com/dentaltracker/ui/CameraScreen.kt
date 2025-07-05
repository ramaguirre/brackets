package com.dentaltracker.ui

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.dentaltracker.R
import com.dentaltracker.utils.CameraUtils
import com.dentaltracker.viewmodel.DentalTrackerViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.io.File
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    viewModel: DentalTrackerViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var isCapturing by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        if (cameraPermissionState.status.isGranted) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                onImageCaptureReady = { capture ->
                    imageCapture = capture
                },
                lifecycleOwner = lifecycleOwner,
                context = context
            )
            
            // Alignment guide overlay
            AlignmentGuideOverlay(
                modifier = Modifier.fillMaxSize()
            )
            
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                
                Text(
                    text = stringResource(R.string.align_teeth),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            MaterialTheme.shapes.small
                        )
                        .padding(8.dp)
                )
            }
            
            // Capture button
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                FloatingActionButton(
                    onClick = {
                        if (!isCapturing) {
                            capturePhoto(
                                context = context,
                                imageCapture = imageCapture,
                                viewModel = viewModel,
                                onCaptureStart = { isCapturing = true },
                                onCaptureComplete = { 
                                    isCapturing = false
                                    onNavigateBack()
                                }
                            )
                        }
                    },
                    modifier = Modifier.size(80.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    if (isCapturing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = Color.White
                        )
                    } else {
                        Icon(
                            Icons.Default.Camera,
                            contentDescription = stringResource(R.string.take_photo),
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                    }
                }
            }
            
        } else {
            // Permission not granted UI
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.camera_permission_required),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Button(
                    onClick = { cameraPermissionState.launchPermissionRequest() }
                ) {
                    Text("Grant Permission")
                }
            }
        }
    }
}

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    onImageCaptureReady: (ImageCapture) -> Unit,
    lifecycleOwner: LifecycleOwner,
    context: Context
) {
    val previewView = remember { PreviewView(context) }
    
    LaunchedEffect(previewView) {
        val cameraProvider = context.getCameraProvider()
        val preview = Preview.Builder().build()
        val imageCapture = ImageCapture.Builder().build()
        
        preview.setSurfaceProvider(previewView.surfaceProvider)
        onImageCaptureReady(imageCapture)
        
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()
        
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    AndroidView(
        factory = { previewView },
        modifier = modifier
    )
}

@Composable
fun AlignmentGuideOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawAlignmentGuide()
    }
}

private fun DrawScope.drawAlignmentGuide() {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val guideColor = Color.White.copy(alpha = 0.7f)
    val strokeWidth = 2.dp.toPx()
    
    // Draw oval guide for mouth alignment
    val ovalWidth = size.width * 0.6f
    val ovalHeight = size.height * 0.3f
    val ovalLeft = centerX - ovalWidth / 2
    val ovalTop = centerY - ovalHeight / 2
    
    drawOval(
        color = guideColor,
        topLeft = Offset(ovalLeft, ovalTop),
        size = Size(ovalWidth, ovalHeight),
        style = Stroke(width = strokeWidth)
    )
    
    // Draw center crosshair
    drawLine(
        color = guideColor,
        start = Offset(centerX - 20.dp.toPx(), centerY),
        end = Offset(centerX + 20.dp.toPx(), centerY),
        strokeWidth = strokeWidth
    )
    
    drawLine(
        color = guideColor,
        start = Offset(centerX, centerY - 20.dp.toPx()),
        end = Offset(centerX, centerY + 20.dp.toPx()),
        strokeWidth = strokeWidth
    )
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

private fun capturePhoto(
    context: Context,
    imageCapture: ImageCapture?,
    viewModel: DentalTrackerViewModel,
    onCaptureStart: () -> Unit,
    onCaptureComplete: () -> Unit
) {
    val imageCapture = imageCapture ?: return
    onCaptureStart()
    
    val photoFile = CameraUtils.createImageFile(context)
    val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    
    imageCapture.takePicture(
        outputFileOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                viewModel.savePhoto(photoFile.absolutePath)
                onCaptureComplete()
            }
            
            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
                onCaptureComplete()
            }
        }
    )
}