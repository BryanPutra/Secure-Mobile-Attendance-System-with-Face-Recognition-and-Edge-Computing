package com.example.Thesis_Project

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.*
import com.example.Thesis_Project.SharedPreferencesConstants.Companion.COUNTING_KEY
import com.example.Thesis_Project.SharedPreferencesConstants.Companion.PREFERENCES
import com.example.Thesis_Project.SharedPreferencesConstants.Companion.START_TIME_KEY
import com.example.Thesis_Project.SharedPreferencesConstants.Companion.STOP_TIME_KEY

class TimerHelper(context: Context)
{
    private var sharedPref: SharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
    private var dateFormat = SimpleDateFormat("dd/mm/yyyy HH:mm:ss", Locale.getDefault())

    private var privateTimerCounting = false
    private var privateStartTime: Date? = null
    private var privateStopTime: Date? = null

    init{
        privateTimerCounting = sharedPref.getBoolean(COUNTING_KEY, false)
        val startString = sharedPref.getString(START_TIME_KEY, null)
        val stopString = sharedPref.getString(STOP_TIME_KEY, null)

        if (startString != null){
            privateStartTime = dateFormat.parse(startString)
        }
        if (stopString != null){
            privateStopTime = dateFormat.parse(stopString)
        }
    }

    fun startTime(): Date? = privateStartTime
    fun setStartTime(date: Date?) {
        privateStartTime = date
        with(sharedPref.edit()){
            val stringDate = if(date == null) null else dateFormat.format(date)
            putString(START_TIME_KEY, stringDate)
            apply()
        }
    }
    fun setStopTime(date: Date?){
        privateStopTime = date
        with(sharedPref.edit()){
            val stringDate = if(date == null) null else dateFormat.format(date)
            putString(STOP_TIME_KEY, stringDate)
            apply()
        }
    }
    fun timerCounting(): Boolean = privateTimerCounting
    fun setTimerCounting(value: Boolean){
        privateTimerCounting = value
        with(sharedPref.edit()){
            putBoolean(COUNTING_KEY, value)
            apply()
        }
    }
}