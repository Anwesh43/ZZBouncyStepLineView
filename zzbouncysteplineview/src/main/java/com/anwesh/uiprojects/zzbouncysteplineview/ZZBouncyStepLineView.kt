package com.anwesh.uiprojects.zzbouncysteplineview

/**
 * Created by anweshmishra on 04/01/20.
 */

import android.view.View
import android.view.MotionEvent
import android.content.Context
import android.app.Activity
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

val nodes : Int = 5
val lines : Int = 3
val strokeFactor : Int = 90
val sizeFactor : Float = 2.9f
val foreColor : Int = Color.parseColor("#9C27B0")
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 30
val scGap : Float = 0.02f / lines
