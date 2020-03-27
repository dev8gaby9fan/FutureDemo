package com.fsh.common.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import com.fsh.common.R

class ScrollableViewPager : ViewPager {
    private var scrollable:Boolean
    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        val obtainStyledAttributes =
            context.obtainStyledAttributes(attrs, R.styleable.ScrollableViewPager)
        scrollable = obtainStyledAttributes.getBoolean(R.styleable.ScrollableViewPager_scrollable,true)
        obtainStyledAttributes.recycle()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return scrollable && super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return scrollable && super.onTouchEvent(ev)
    }

}