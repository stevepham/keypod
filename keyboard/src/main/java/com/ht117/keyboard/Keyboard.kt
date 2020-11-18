package com.ht117.keyboard

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

data class KeyboardInfo(val isVisible: Boolean, val contentHeight: Int, val contentHeightBeforeResize: Int)

object KeyboardHandler {

    private var detector: Detector? = null
    private var heightAnimator: ValueAnimator = ObjectAnimator()
    private var holder: ScreenHolder? = null

    fun watch(activity: Activity) {
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        heightAnimator.run {
            cancel()
            removeAllUpdateListeners()
        }
        holder = activity.createScreenHolder().apply {
            detector = Detector(this) { animateHeight(it) }
            decorView.viewTreeObserver.addOnPreDrawListener(detector)
            onDetach { decorView.viewTreeObserver.addOnPreDrawListener(detector) }
        }
    }

    fun unwatch() {
        holder?.decorView?.viewTreeObserver?.removeOnPreDrawListener(detector)
        detector = null
        holder = null
    }

    private fun animateHeight(event: KeyboardInfo) {
        holder?.run {
            contentView.setHeight(event.contentHeightBeforeResize)
            heightAnimator.cancel()

            heightAnimator = ObjectAnimator.ofInt(event.contentHeightBeforeResize, event.contentHeight).apply {
                interpolator = FastOutSlowInInterpolator()
                duration = 350
                addUpdateListener { contentView.setHeight(it.animatedValue as Int) }
                start()
            }
        }
    }

    private fun View.setHeight(height: Int) {
        val params = layoutParams
        params.height = height
        layoutParams = params
    }

    private class Detector(val holder: ScreenHolder,
                           val listener: (KeyboardInfo) -> Unit): ViewTreeObserver.OnPreDrawListener {

        private var preHeight = -1
        override fun onPreDraw(): Boolean {
            val detected = detect()
            return detected.not()
        }

        private fun detect(): Boolean {
            val contentHeight = holder.contentViewFrame.height
            if (contentHeight == preHeight) {
                return false
            }

            if (preHeight != -1) {
                val statusBarHeight = holder.contentViewFrame.top
                val isKeyboardVisible = contentHeight < holder.decorView.height - statusBarHeight

                listener(KeyboardInfo(
                    isVisible = isKeyboardVisible,
                    contentHeight = contentHeight,
                    contentHeightBeforeResize = preHeight)
                )
            }

            preHeight = contentHeight
            return true
        }
    }
}