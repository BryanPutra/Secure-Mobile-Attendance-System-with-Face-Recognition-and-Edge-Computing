package com.example.mvp.ui.register

import android.graphics.*
import androidx.lifecycle.ViewModel
import kotlin.math.sqrt

class RegisterViewModel : ViewModel() {
    var imgBitmap: Bitmap? = null

    fun setBitmap(bitmap: Bitmap?){
        imgBitmap = bitmap
    }

    fun clearBitmap(){
        imgBitmap = null
    }

}