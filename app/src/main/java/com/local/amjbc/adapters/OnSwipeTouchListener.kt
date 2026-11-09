package com.local.amjbc.adapters

import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import kotlin.math.abs

open class OnSwipeTouchListener : OnTouchListener {

    private var downX = 0f
    private var downY = 0f

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                // Return false so parent ScrollView can receive vertical drag events
                return false
            }
            MotionEvent.ACTION_UP -> {
                val upX = event.x
                val upY = event.y
                val diffX = upX - downX
                val diffY = upY - downY

                if (abs(diffX) > abs(diffY) && abs(diffX) > SWIPE_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight()
                    } else {
                        onSwipeLeft()
                    }
                    return true
                } else if (abs(diffY) <= SWIPE_THRESHOLD && abs(diffX) <= SWIPE_THRESHOLD) {
                    onClick()
                    return false
                }
            }
        }
        return false
    }

    open fun onSwipeRight() {}
    open fun onSwipeLeft() {}
    open fun onSwipeTop() {}
    open fun onSwipeBottom() {}
    open fun onClick() {}

    companion object {
        private const val SWIPE_THRESHOLD = 80
    }
}