package com.fsh.common.ext

import android.view.GestureDetector
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/11
 * description: TODO there need some info to descript current java file
 *
 */

class RecyclerViewItemClickListener(recyclerView:RecyclerView,var listener:OnItemTouchEventListener?) : RecyclerView.SimpleOnItemTouchListener(){
    private var gestureDetector:GestureDetector =
        GestureDetector(recyclerView.context,object : GestureDetector.SimpleOnGestureListener(){
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                if(listener == null){
                    return super.onSingleTapConfirmed(e)
                }else{
                    val childView = recyclerView.findChildViewUnder(e!!.x, e!!.y)
                    val childLayoutPosition = recyclerView.getChildLayoutPosition(childView!!)
                    listener?.onClick(childLayoutPosition)
                    return true
                }
            }

            override fun onLongPress(e: MotionEvent?) {
                val childView = recyclerView.findChildViewUnder(e!!.x, e!!.y)
                val childLayoutPosition = recyclerView.getChildLayoutPosition(childView!!)
                listener?.onLongClick(childLayoutPosition)
            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                return false
            }
        })

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        gestureDetector.onTouchEvent(e)
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(e)
    }
}

interface OnItemTouchEventListener{
    fun onClick(position:Int)

    fun onLongClick(position:Int)
}