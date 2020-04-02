package com.fsh.trade.widget.keyboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.inputmethodservice.KeyboardView
import android.util.AttributeSet
import com.fsh.common.util.Omits
import com.fsh.trade.R

class FutureKeyboardView : KeyboardView{
    private var keyDrawable:Drawable? = null
    private var paint:Paint? = null
    private val rect:Rect = Rect()
    private var distance:Int =0
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes){
        parseTypedArray(context,attrs)
    }

    private fun parseTypedArray(context: Context?,attrs: AttributeSet?){
        val styledAttrs =
            context?.obtainStyledAttributes(attrs, R.styleable.FutureKeyboardView)
        keyDrawable = styledAttrs?.getDrawable(R.styleable.FutureKeyboardView_keyDrawable)
        val keyTextSize = styledAttrs?.getDimensionPixelSize(R.styleable.FutureKeyboardView_keyTextSize,
            context.resources.getDimensionPixelSize(R.dimen.abc_sp_14))
        val keyTextColor = styledAttrs?.getColor(R.styleable.FutureKeyboardView_keyTextColor,
            context?.getColor(R.color.dark_light))
        styledAttrs?.recycle()
        distance = context!!.resources.getDimensionPixelSize(R.dimen.abc_dp_10)
        paint = Paint().apply {
            textSize = keyTextSize!!.toFloat()
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            color = keyTextColor!!
        }
    }

    override fun onDraw(canvas: Canvas?) {
        for(key in keyboard.keys){
            canvas?.save()
            var offsetY = 0
            if(key.y == 0){
                offsetY = 4
            }
            val drawY = offsetY + key.y
            if(keyDrawable != null){
                rect.left = key.x
                rect.top = drawY
                rect.bottom = key.y + key.height
                rect.right = key.x + key.width
                canvas?.clipRect(rect)
                val state = key.currentDrawableState
                keyDrawable?.state = state
                keyDrawable?.bounds = rect
                keyDrawable?.draw(canvas!!)
            }
            if(!Omits.isOmit(key.label?.toString())){
                canvas?.drawText(key.label.toString(),
                    (key.x+key.width/2).toFloat(),drawY+(key.height+ paint?.textSize!! - paint?.descent()!!)/2,
                    paint!!
                )
            }else if(key.icon != null){
                val drawableWidth = key.icon.intrinsicWidth
                val drawableHeight = key.icon.intrinsicHeight
                key.icon.setBounds(
                    key.x +(key.width-drawableWidth)/2,
                    drawY + (key.height - drawableHeight)/2,
                    key.x +(key.width-drawableWidth)/2+drawableWidth,
                    drawY + (key.height - drawableHeight)/2+drawableHeight
                )
                key.icon.draw(canvas!!)
            }
            canvas?.restore()
        }
    }



}