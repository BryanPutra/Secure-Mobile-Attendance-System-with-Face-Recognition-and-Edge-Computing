package com.example.mvp.ui.detect

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModelProvider
import com.example.mvp.BitmapUtils
import com.example.mvp.Model
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions


class FrameAnalyzer ( private val context: Context, activity: DetectFragment,
    private var model: Model
) : ImageAnalysis.Analyzer {
    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
        .build()
    private val faceDetector = FaceDetection.getClient(options)
    private var frameAnalyzerViewModel = ViewModelProvider(activity)[DetectViewModel::class.java]

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        var bitmap = BitmapUtils.getBitmap(image)
        bitmap = Model.toGrayscale(bitmap!!)
        val inputImage = InputImage.fromBitmap(bitmap,0)
        faceDetector.process(inputImage).addOnSuccessListener{ faces->
            if(faces.size == 0){
                frameAnalyzerViewModel.resetStatus()
                return@addOnSuccessListener
            }
            for(i in faces){
                val liveness = frameAnalyzerViewModel.checkLiveness(i.leftEyeOpenProbability!!,i.rightEyeOpenProbability!!)
                if(liveness == 1)
                {
                    val bounds = i.boundingBox
                    try {
                        var croppedBitmap = Bitmap.createBitmap(bitmap!!, bounds.left, bounds.top, bounds.width(), bounds.height())
                        croppedBitmap = Bitmap.createScaledBitmap(croppedBitmap,Model.modelInput,Model.modelInput,true)
                        val result = model.compareFace(croppedBitmap)
                        if(result[0] == "true"){
                            Toast.makeText(context, "Pass " +result[1], Toast.LENGTH_SHORT).show()
                        } else{
                            Toast.makeText(context,"Fail " + result[1] , Toast.LENGTH_SHORT).show()
                        }

                    } catch (ex: IllegalArgumentException){
                        Toast.makeText(context,"Face too close to camera",Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }
                    return@addOnSuccessListener
                } else if(liveness == -1){
                    Toast.makeText(context,"Please blink to ensure liveness", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnCompleteListener{
            image.close()
        }
    }
}

