package com.anwesh.uiprojects.linkedzigzagbouncysteplineview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.zzbouncysteplineview.ZZBouncyStepLineView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ZZBouncyStepLineView.create(this)
    }
}
