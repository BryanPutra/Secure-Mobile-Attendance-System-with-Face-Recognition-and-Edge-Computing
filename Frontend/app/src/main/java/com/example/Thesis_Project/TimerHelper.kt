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

    private var timerCounting by mutableStateOf(false)
    private var startTime: Date? by mutableStateOf(null)
    private var stopTime: Date? by mutableStateOf(null)

    init{
        timerCounting = sharedPref.getBoolean(COUNTING_KEY, false)
        val startString = sharedPref.getString(START_TIME_KEY, null)
        val stopString = sharedPref.getString(STOP_TIME_KEY, null)

        if (startString != null){
            startTime = dateFormat.parse(startString)
        }
        if (stopString != null){
            stopTime = dateFormat.parse(stopString)
        }
    }

    fun startTime(): Date? = startTime
    fun setStartTime(date: Date?) {
        startTime = date
        with(sharedPref.edit()){
            val stringDate = if(date == null) null else dateFormat.format(date)
            putString(STOP_TIME_KEY, stringDate)
            apply()
        }
    }
    fun setStopTime(date: Date?){
        stopTime = date
        with(sharedPref.edit()){
            val stringDate = if(date == null) null else dateFormat.format(date)
            putString(STOP_TIME_KEY, stringDate)
            apply()
        }
    }
    fun timerCounting(): Boolean = timerCounting
    fun setTimerCounting(value: Boolean){
        timerCounting = value
        with(sharedPref.edit()){
            putBoolean(COUNTING_KEY, value)
            apply()
        }
    }
}