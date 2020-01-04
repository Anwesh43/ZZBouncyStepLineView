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

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawZZBouncyStepLine(i : Int, scale : Float, size : Float, paint : Paint) {
    val sf : Float = scale.sinify().divideScale(i, lines)
    val j : Int = i / 2
    val i2 : Int = i % 2
    val k : Int =  i2 + 1
    val j1 : Int = (i + 1) / 2
    save()
    translate(-size / 2 + size * j, -size + size * j1)
    drawLine(0f, 0f, size * sf * i2, size * sf * k, paint)
    restore()
}

fun Canvas.drawZZBouncyStepLines(scale : Float, size : Float, paint : Paint) {
    for (j in 0..(lines - 1)) {
        drawZZBouncyStepLine(j, scale, size, paint)
    }
}

fun Canvas.drawZZBSLNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes + 1)
    val size : Float = gap / sizeFactor
    paint.color = foreColor
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    save()
    translate(gap * (i + 1), h / 2)
    drawZZBouncyStepLines(scale, size, paint)
    restore()
}

class ZZBouncyStepLineView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class ZZBSLNode(var i : Int, val state : State = State()) {


        private var next : ZZBSLNode? = null
        private var prev : ZZBSLNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = ZZBSLNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawZZBSLNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb  : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit): ZZBSLNode {
            var curr : ZZBSLNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class ZZBouncyStepLine(var i : Int) {

        private val root : ZZBSLNode = ZZBSLNode(0)
        private var curr : ZZBSLNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : ZZBouncyStepLineView) {

        private val zzbsl : ZZBouncyStepLine = ZZBouncyStepLine(0)
        private val animator : Animator = Animator(view)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            zzbsl.draw(canvas, paint)
            animator.animate {
                zzbsl.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            zzbsl.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity: Activity) : ZZBouncyStepLineView {
            val view : ZZBouncyStepLineView = ZZBouncyStepLineView(activity)
            activity.setContentView(view)
            return view
        }
    }
}