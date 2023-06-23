package com.example.Thesis_Project.ui.screens.camera

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.Composable
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.suspendCancellableCoroutine
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.example.Thesis_Project.backend.camera.BitmapUtils
import com.example.Thesis_Project.backend.camera.Model
import com.example.Thesis_Project.backend.db.db_models.User
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.components.ButtonHalfWidth
import com.example.Thesis_Project.viewmodel.MainViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.common.internal.ImageConvertUtils
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.concurrent.Executor

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun CameraView(
    executor: Executor,
    onError: (ImageCaptureException) -> Unit,
    mainViewModel: MainViewModel
) {

    fun takePhoto(
        imageCapture: ImageCapture,
        executor: Executor,
        onError: (ImageCaptureException) -> Unit,
        mainViewModel: MainViewModel
    ) {

        imageCapture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onError(exception: ImageCaptureException) {
                Log.e("kilo", "Take photo error:", exception)
                onError(exception)
            }

            @SuppressLint("UnsafeOptInUsageError")
            override fun onCaptureSuccess(image: ImageProxy) {
                mainViewModel.clearBitmap()
                val newImage = image.image
                val inputImage =
                    InputImage.fromMediaImage(newImage!!, image.imageInfo.rotationDegrees)
                mainViewModel.setBitmap(
                    ImageConvertUtils.getInstance().getUpRightBitmap(inputImage)
                )
                Log.d("imagebitmapset", "${mainViewModel.imgBitmap}")
                mainViewModel.setHasTakenPicture(true)
                image.close()
            }
        })
    }

    suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCancellableCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get(), null)
            }, ContextCompat.getMainExecutor(this))
        }
    }

    // 1
    val lensFacing = CameraSelector.LENS_FACING_FRONT
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

//    val mainViewModel.imgBitmap by rememberSaveable { mutableStateOf(mainViewModel.imgBitmap) }

    // 2
    LaunchedEffect(mainViewModel.hasTakenPicture) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
        preview.setSurfaceProvider(previewView.surfaceProvider)
        Log.d("restart component", "ahwuda")
    }

    fun contentValues() : ContentValues {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values
    }

    fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveImage(bitmap: Bitmap, context: Context, folderName: String) {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            val values = contentValues()
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + folderName)
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            // RELATIVE_PATH and IS_PENDING are introduced in API 29.

            val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                saveImageToStream(bitmap, context.contentResolver.openOutputStream(uri))
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                context.contentResolver.update(uri, values, null, null)
            }
        } else {
            val directory = File(Environment.getExternalStorageDirectory().toString() + File.separator + folderName)
            // getExternalStorageDirectory is deprecated in API 29

            if (!directory.exists()) {
                directory.mkdirs()
            }
            val fileName = System.currentTimeMillis().toString() + ".png"
            val file = File(directory, fileName)
            saveImageToStream(bitmap, FileOutputStream(file))
            if (file.absolutePath != null) {
                val values = contentValues()
                values.put(MediaStore.Images.Media.DATA, file.absolutePath)
                // .DATA is deprecated in API 29
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            }
        }
    }

    fun savePicture() {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .build()
        val faceDetector = FaceDetection.getClient(options)
        var bitmap = Bitmap.createScaledBitmap(mainViewModel.imgBitmap!!, 600, 800, true)
        bitmap = BitmapUtils.toGrayscale(bitmap)
        saveImage(bitmap, context, "Test")
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        faceDetector.process(inputImage).addOnSuccessListener { faces ->
            if (faces.size == 0) {
                Toast.makeText(context, "No face detected", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }
            for (i in faces) {
                val bounds = i.boundingBox
                try {
                    var croppedBitmap = Bitmap.createBitmap(
                        bitmap,
                        bounds.left,
                        bounds.top,
                        bounds.width(),
                        bounds.height()
                    )
                    croppedBitmap = Bitmap.createScaledBitmap(
                        croppedBitmap,
                        Model.modelInput,
                        Model.modelInput,
                        true
                    )
                    Toast.makeText(context, "Face successfully saved", Toast.LENGTH_SHORT).show()
                    val model = Model(context)
                    model.registerFace(croppedBitmap)
                    val savedEmbs = File(context.filesDir, "embsKnown")
                    Log.d("savedembds", "${savedEmbs.exists()}")
                    val contents = savedEmbs.readText()
                    mainViewModel.setUserEmbeddings(contents)
                    db_util.registerFace(mainViewModel.db, mainViewModel.userData?.userid!!, contents)

                } catch (ex: IllegalArgumentException) {
                    Toast.makeText(context, "Face too close to camera", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                return@addOnSuccessListener
            }
        }

    }

    // 3
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (!mainViewModel.hasTakenPicture) {
                AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
            } else {
                Image(bitmap = mainViewModel.imgBitmap!!.asImageBitmap(), contentDescription = null)
            }
        }
        Box(
            modifier = Modifier
                .background(Color.Transparent)
                .wrapContentHeight()
                .zIndex(2f)
                .align(Alignment.BottomCenter)
                .padding(bottom = MaterialTheme.spacing.spaceExtraLarge),
            contentAlignment = Alignment.Center
        ) {
            if (mainViewModel.hasTakenPicture) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(0.5f)) {
                        ButtonHalfWidth(onClick = {
                            savePicture()
                        }, buttonText = "Save")
                    }
                    Box(modifier = Modifier.weight(0.5f)) {
                        ButtonHalfWidth(onClick = {
                            mainViewModel.setHasTakenPicture(false)
                        }, buttonText = "Retake")
                    }
                }
            }
            else {
                ButtonHalfWidth(onClick = {
                    takePhoto(
                        imageCapture = imageCapture,
                        executor = executor,
                        onError = onError,
                        mainViewModel = mainViewModel
                    )
                }, buttonText = "Take Photo")
            }
        }
    }
}