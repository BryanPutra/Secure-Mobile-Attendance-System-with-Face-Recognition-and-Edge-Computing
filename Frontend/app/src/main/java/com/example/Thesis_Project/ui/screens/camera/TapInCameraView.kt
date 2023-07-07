package com.example.Thesis_Project.ui.screens.camera

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
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
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.suspendCancellableCoroutine
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.Thesis_Project.R
import com.example.Thesis_Project.backend.camera.BitmapUtils
import com.example.Thesis_Project.backend.camera.Model
import com.example.Thesis_Project.backend.db.db_models.User
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.components.ButtonHalfWidth
import com.example.Thesis_Project.ui.components.CircularLoadingBar
import com.example.Thesis_Project.ui.components.ConnectCompanyWifiBlocker
import com.example.Thesis_Project.ui.utils.checkDateIsHoliday
import com.example.Thesis_Project.ui.utils.checkDateIsWeekend
import com.example.Thesis_Project.viewmodel.MainViewModel
import com.example.mvp.ui.detect.FrameAnalyzer
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.common.internal.ImageConvertUtils
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*
import java.util.concurrent.Executor

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun TapInCameraView(
    executor: Executor,
    onError: (ImageCaptureException) -> Unit,
    mainViewModel: MainViewModel,
    navController: NavController
) {

    var isConnectedToCompanyWifi by rememberSaveable { mutableStateOf(false) }

    fun getConnectedWifiBssid(context: Context): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return ""
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(network) ?: return ""

            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                val wifiManager =
                    context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo
                return wifiInfo.bssid
            }
            return ""
        }
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo: WifiInfo = wifiManager.connectionInfo
        return wifiInfo.bssid
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
    val model = Model(context)
    val preview = Preview.Builder().build()
    val imageAnalyzer =
        ImageAnalysis.Builder().setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

    val previewView = remember { PreviewView(context) }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

//    val mainViewModel.imgBitmap by rememberSaveable { mutableStateOf(mainViewModel.imgBitmap) }

    // 2
    LaunchedEffect(Unit) {
        while (true) {
            isConnectedToCompanyWifi = if (getConnectedWifiBssid(context).isEmpty()) {
                false
            } else {
                mainViewModel.companyVariable?.wifissid?.contains(getConnectedWifiBssid(context)) == true
            }
            delay(1000L) // Delay for 1 minute (60000 milliseconds)
        }
    }

    LaunchedEffect(isConnectedToCompanyWifi) {
        val cameraProvider = context.getCameraProvider()
        if (isConnectedToCompanyWifi){
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalyzer
            )
            imageAnalyzer.setAnalyzer(
                executor,
                FrameAnalyzer(context, mainViewModel, model, navController)
            )
            preview.setSurfaceProvider(previewView.surfaceProvider)
        }
    }

    // 3
    Box(modifier = Modifier.fillMaxSize()) {

        if (!isConnectedToCompanyWifi) {
            ConnectCompanyWifiBlocker()
        }
        else {
            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
            }
        }
        Box(
            modifier = Modifier
                .background(Color.Transparent)
                .wrapContentHeight()
                .zIndex(2f)
                .align(Alignment.TopCenter)
                .padding(top = MaterialTheme.spacing.spaceXXXLarge),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Scan your face",
                style = MaterialTheme.typography.headlineMedium,
                color = colorResource(
                    id = R.color.blue_500
                )
            )
        }
        if (mainViewModel.isLoading) {
            CircularLoadingBar()
        }
    }


}