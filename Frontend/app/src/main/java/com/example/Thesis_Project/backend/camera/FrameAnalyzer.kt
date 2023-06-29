package com.example.mvp.ui.detect

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.navigation.NavController
import com.example.Thesis_Project.TimerHelper
import com.example.Thesis_Project.backend.camera.BitmapUtils
import com.example.Thesis_Project.backend.camera.Model
import com.example.Thesis_Project.backend.db.db_models.Attendance
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.ui.navgraphs.NavGraphs
import com.example.Thesis_Project.viewmodel.MainViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File


class FrameAnalyzer (private val context: Context, val mainViewModel: MainViewModel,
                     private var model: Model, val navController: NavController
) : ImageAnalysis.Analyzer {
    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
        .build()
    private val faceDetector = FaceDetection.getClient(options)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        var bitmap = BitmapUtils.getBitmap(image)
        bitmap = BitmapUtils.toGrayscale(bitmap!!)
        val inputImage = InputImage.fromBitmap(bitmap,0)
        faceDetector.process(inputImage).addOnSuccessListener{ faces->
            if(faces.size == 0){
                mainViewModel.resetStatus()
                return@addOnSuccessListener
            }
            for(i in faces){
                val liveness = mainViewModel.checkLiveness(i.leftEyeOpenProbability!!,i.rightEyeOpenProbability!!)
                if(liveness == 1)
                {
                    val bounds = i.boundingBox
                    try {
                        var croppedBitmap = Bitmap.createBitmap(bitmap, bounds.left, bounds.top, bounds.width(), bounds.height())
                        croppedBitmap = Bitmap.createScaledBitmap(croppedBitmap,Model.modelInput,Model.modelInput,true)
                        val result = model.compareFace(croppedBitmap)
                        if(result[0] == "true"){
                            val attendance = Attendance(
                                userid = mainViewModel.userData?.userid,
                                leaveflag = false,
                                permissionflag = false,
                                absentflag = false,
                                timein = db_util.curDateTime()
                            )
                            Toast.makeText(context, "Pass " +result[1], Toast.LENGTH_SHORT).show()
                            val savedEmbs = File(context.filesDir, "embsKnown")
                            val contents = savedEmbs.readText()
                            mainViewModel.setUserEmbeddings(contents)
                            db_util.registerFace(mainViewModel.db, mainViewModel.userData?.userid!!, contents)
                            db_util.createAttendance(mainViewModel.db, attendance ,
                                mainViewModel.userData!!
                            )
                            Toast.makeText(context, "Face recognized", Toast.LENGTH_SHORT).show()
                            mainViewModel.startWorkHourTimer(mainViewModel.timerHelper)
                            mainViewModel.setIsTappedIn(true)
                            mainViewModel.setTapInDisabled(true)
                            navController.popBackStack()
                            navController.navigate(NavGraphs.HOME)
                            // create attendance
                        } else{
//                            Toast.makeText(context,"Fail " + result[1] , Toast.LENGTH_SHORT).show()
                            Toast.makeText(context,"Invalid face detected", Toast.LENGTH_SHORT).show()
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

