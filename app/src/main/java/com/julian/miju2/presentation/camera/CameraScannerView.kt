package com.julian.miju2.presentation.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.julian.miju2.R


@Composable
fun CameraScannerScreen(
    onTextScanned: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        if (hasPermission) {
            CameraContent(onTextScanned = onTextScanned, onBack = onBack)
        } else {
            PermissionDenied(
                onRetry = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                onBack = onBack
            )
        }
    }
}

@Composable
private fun PermissionDenied(onRetry: () -> Unit, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.camera_permission_required),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(stringResource(id = R.string.camera_grant_permission))
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onBack) {
            Text(stringResource(id = R.string.camera_back), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CameraContent(
    onTextScanned: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isProcessing by remember { mutableStateOf(false) }

    // LifecycleCameraController NO se enlaza solo: hay que llamar bindToLifecycle().
    // Lo hacemos una sola vez aquí (keyed por lifecycleOwner); el controller registra
    // internamente un LifecycleObserver, así que se desenlaza automáticamente al
    // destruirse el lifecycle. Por eso NO se necesita un DisposableEffect con unbind().
    val controller = remember(lifecycleOwner) {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(LifecycleCameraController.IMAGE_CAPTURE)
            bindToLifecycle(lifecycleOwner)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PreviewView(ctx).apply {
                    this.controller = controller
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
            }
        )

        Text(
            text = stringResource(id = R.string.camera_instruction),
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp, start = 24.dp, end = 24.dp)
        )

        TextButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
        ) {
            Text(stringResource(id = R.string.camera_back), color = Color.White)
        }

        Button(
            onClick = {
                if (isProcessing) return@Button
                isProcessing = true
                captureAndRecognize(
                    controller = controller,
                    context = context,
                    onResult = { text ->
                        isProcessing = false
                        onTextScanned(text)
                    },
                    onError = { isProcessing = false }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .fillMaxWidth(0.7f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            enabled = !isProcessing
        ) {
            if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    stringResource(id = R.string.camera_capture),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

/**
 * Toma una foto única con [controller], la convierte a [InputImage] y corre
 * el reconocedor de texto latino de ML Kit. Devuelve el texto crudo o invoca
 * [onError] si la captura o el reconocimiento fallan.
 */
private fun captureAndRecognize(
    controller: LifecycleCameraController,
    context: Context,
    onResult: (String) -> Unit,
    onError: () -> Unit
) {
    val executor = ContextCompat.getMainExecutor(context)
    controller.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {
            @OptIn(ExperimentalGetImage::class)
            override fun onCaptureSuccess(imageProxy: ImageProxy) {
                val mediaImage = imageProxy.image
                if (mediaImage == null) {
                    imageProxy.close()
                    onError()
                    return
                }
                val inputImage = InputImage.fromMediaImage(
                    mediaImage,
                    imageProxy.imageInfo.rotationDegrees
                )
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                recognizer.process(inputImage)
                    .addOnSuccessListener { visionText ->
                        onResult(visionText.text)
                    }
                    .addOnFailureListener {
                        onError()
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            }

            override fun onError(exception: ImageCaptureException) {
                onError()
            }
        }
    )
}
