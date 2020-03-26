package com.fsh.trade.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.fsh.common.util.Omits
import com.fsh.common.util.SizeUtils
import com.fsh.trade.R

class OrderButton : View {
    private val textPaint:Paint
    private var orderPrice:String = Omits.OmitPrice
    private var orderText:String = Omits.OmitString
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    @SuppressLint("ResourceType")
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {

        val typedArray = context?.obtainStyledAttributes(attrs, R.styleable.OrderButton)
        orderPrice =
            typedArray?.getString(R.styleable.OrderButton_orderPrice) ?: Omits.OmitPrice
        orderText = typedArray?.getString(R.styleable.OrderButton_orderText) ?: Omits.OmitString
        val textColor = typedArray?.getColor(R.styleable.OrderButton_textColor,context.resources.getColor(R.color.dark_deep)) ?:context!!.resources.getColor(R.color.dark_deep)
        val textSize = typedArray?.getDimensionPixelSize(R.styleable.OrderButton_textSize,SizeUtils.dp2px(14)) ?:SizeUtils.dp2px(14)
        typedArray?.recycle()

        textPaint = Paint()
        textPaint.isAntiAlias = true
        textPaint.color = textColor
        textPaint.textSize = textSize.toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(getDefaultSize(suggestedMinimumWidth,widthMeasureSpec),getHeightSize(heightMeasureSpec))
    }

    private fun getHeightSize(heightMeasureSpec: Int):Int{
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        return when(heightMode){
            MeasureSpec.UNSPECIFIED -> context.resources.getDimensionPixelSize(R.dimen.abc_dp_60)
            else -> height
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //绘制第一行的文字 委托价格
        val orderPriceBounds = Rect()
        textPaint.getTextBounds(orderPrice,0,orderPrice.length,orderPriceBounds)
        canvas?.drawText(orderPrice,((measuredWidth - orderPriceBounds.width())/2.0f),orderPriceBounds.top+(measuredHeight/4.0f-orderPriceBounds.height()/2.0f),textPaint)
        //第二行的文字
        val orderTextBounds = Rect()
        textPaint.getTextBounds(orderText,0,orderText.length,orderTextBounds)
        canvas?.drawText(orderText,(measuredWidth - orderTextBounds.width())/2.0f,orderPriceBounds.top+(measuredHeight/2.0f+measuredHeight/4.0f-orderTextBounds.height()/2.0f),textPaint)
    }

    fun setOrderPriceText(price:String){
        orderPrice = price
        postInvalidate()
    }

    fun setOrderButtonText(buttonText:String){
        orderPrice = buttonText
        postInvalidate()
    }
}