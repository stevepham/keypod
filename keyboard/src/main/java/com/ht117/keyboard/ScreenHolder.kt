package com.ht117.keyboard

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.Window

data class ScreenHolder(val decorView: ViewGroup,
                        val contentViewFrame: ViewGroup,
                        val contentView: ViewGroup) {

    fun onDetach(onDetach: () -> Unit) {
        decorView.addOnAttachStateChangeListener(object: View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(p0: View?) {
                onDetach
            }

            override fun onViewDetachedFromWindow(p0: View?) {
            }

        })
    }
}

fun Activity.createScreenHolder(): ScreenHolder {
    val decorView = window.decorView as ViewGroup
    val decorViewChild = decorView.getChildAt(0) as ViewGroup
    val contentViewFrame = decorViewChild.getChildAt(1) as ViewGroup
    val contentView = decorView.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)

    return ScreenHolder(decorView = decorView, contentViewFrame = contentViewFrame, contentView = contentView)
}