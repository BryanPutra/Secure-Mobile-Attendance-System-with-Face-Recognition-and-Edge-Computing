package com.example.mvp.ui.register

import android.graphics.*
import androidx.lifecycle.ViewModel
import kotlin.math.sqrt

class RegisterViewModel : ViewModel() {
    var imgBitmap: Bitmap? = null
    private var modelStatus = 0 // 0 MobileFaceNet, 1 FaceNet

    fun setBitmap(bitmap: Bitmap?){
        imgBitmap = bitmap
    }

    fun clearBitmap(){
        imgBitmap = null
    }

    fun changeModel(): Int{
        modelStatus = if(modelStatus == 0) 1 else 0
        return modelStatus
    }

    fun getStatus():Int{
        return modelStatus
    }
}