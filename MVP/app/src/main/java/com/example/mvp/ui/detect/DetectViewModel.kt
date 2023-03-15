package com.example.mvp.ui.detect

import androidx.lifecycle.ViewModel
import kotlin.math.pow
import kotlin.math.sqrt

class DetectViewModel : ViewModel() {
    private var liveProbabilities: ArrayList<Float> = ArrayList()
    private var status = 0

    fun checkLiveness(probLeft: Float, probRight: Float): Int {
        if(liveProbabilities.size < 50) {
            liveProbabilities.add(probLeft)
            liveProbabilities.add(probRight)
            return 0 // Not enough numbers
        }
        val stdTemp = std()
        if(stdTemp >= 0.2f){
            status = 1
            liveProbabilities.clear()
            return if(status == 0) -1 else 1 // Passed detection after second loop
        }
        else{
            liveProbabilities.clear()
            return -1 // Failed detection
        }
    }

    fun resetStatus(){
        liveProbabilities.clear()
        status = 0
    }

     private fun std(): Float {
        var total = 0f
        var tempStd = 0f
        for(i in liveProbabilities){
            total += i
        }
        val mean = total/liveProbabilities.size
        for(i in liveProbabilities){
            tempStd += (i - mean).pow(2)
        }
        return sqrt(tempStd/liveProbabilities.size)
    }

}