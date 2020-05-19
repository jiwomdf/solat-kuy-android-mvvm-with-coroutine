package com.programmergabut.solatkuy.customview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/*
 * Created by Katili Jiwo Adi Wiyono on 10/05/20.
 */

class NonSwipeAbleViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean = false

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean = false
}