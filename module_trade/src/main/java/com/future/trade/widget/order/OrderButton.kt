package com.future.trade.widget.order

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.fsh.common.base.BaseActivity
import com.fsh.common.model.InstrumentInfo
import com.fsh.common.util.ARouterUtils
import com.fsh.common.util.Omits
import com.fsh.common.util.SizeUtils
import com.future.trade.R
import com.future.trade.bean.CTPInputOrderActionField
import com.future.trade.bean.CTPOrderInsertField
import com.future.trade.bean.IInputOrderActionField
import com.future.trade.bean.IOrderInsertField
import com.future.trade.enums.*
import com.future.trade.model.SupportTransactionOrderPrice
import com.future.trade.repository.TradeApiProvider
import com.future.trade.widget.dialog.OrderInsertNoticeDialog
import com.google.android.material.snackbar.Snackbar
import java.lang.IllegalArgumentException
import java.lang.NumberFormatException


abstract class OrderButton : View {
    private val textPaint:Paint
    private var orderPrice:String = Omits.OmitPrice
    private var orderText:String = Omits.OmitString
    private var lastClickTime:Long = Omits.OmitLong
    protected var orderInstrument:InstrumentInfo? = null
    private var combOffset:CTPCombOffsetFlag
    private var direction:CTPDirection
    private var priceType:SupportTransactionOrderPrice = SupportTransactionOrderPrice.Opponent
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
        val offset = typedArray?.getInt(R.styleable.OrderButton_combOffset,CTPCombOffsetFlag.Open.offset.toInt()) ?: CTPCombOffsetFlag.Open.offset.toInt()
        val dir = typedArray?.getInt(R.styleable.OrderButton_direction,CTPDirection.Buy.direction.toInt()) ?: CTPDirection.Buy.direction.toInt()
        typedArray?.recycle()
        combOffset = CTPCombOffsetFlag.from(offset.toChar())
        direction = CTPDirection.from(dir.toChar())!!

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
        val fontMetrics = textPaint.fontMetrics
        val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        val baseline = measuredHeight/4.0f + distance
        canvas?.drawText(orderPrice,((measuredWidth - orderPriceBounds.width())/2.0f),
            baseline,textPaint)
        //第二行的文字
        val orderTextBounds = Rect()
        textPaint.getTextBounds(orderText,0,orderText.length,orderTextBounds)
        val baseline2 = measuredHeight/4.0f*3.0f + distance
        canvas?.drawText(orderText,0,orderText.length,(measuredWidth - orderTextBounds.width())/2.0f,baseline2,textPaint)
    }

    fun setOrderPriceText(price:String){
        orderPrice = price
        postInvalidate()
    }

    /**
     * 检查报单参数
     */
    @Throws(IllegalArgumentException::class)
    fun checkOrderParams(volume:String?){
        //1.判断用户是不是没有登录
        checkUserSession()
        //2.检查委托的价格是不是在涨跌停范围\市价单判断
        checkOrderPrice()
        //3.检查委托数量
        checkOrderVolume(volume)
    }

    @Throws(IllegalArgumentException::class)
    private fun checkUserSession(){
        //检查是不是没有登录
        val tradeAPI = TradeApiProvider.providerCTPTradeApi()
        tradeAPI.getCurrentUser() ?: throw IllegalArgumentException(resources.getString(R.string.notice_not_login))
    }

    @Throws(IllegalArgumentException::class)
    private fun checkOrderPrice(){
        if(orderInstrument == null){
            throw IllegalArgumentException(resources.getString(R.string.notice_no_instrument))
        }
        if(Omits.isOmit(orderPrice) || orderPrice.toDouble() < 0){
            throw IllegalArgumentException(resources.getString(R.string.notice_price_error))
        }
        val exchange = ExchangeType.from(orderInstrument?.id)
        if(priceType == SupportTransactionOrderPrice.Market){
            //市价单价格不对
            if(exchange != ExchangeType.SHFE && exchange != ExchangeType.INE && orderPrice.toDouble() != 0.0){
                throw IllegalArgumentException(resources.getString(R.string.notice_market_price_fail))
            }
        }
        val quoteEntity = ARouterUtils.getQuoteService().getQuoteByInstrument(orderInstrument?.id)
        if(quoteEntity != null){
            val upLimitPrice = quoteEntity!!.upper_limit
            val lowLimit = quoteEntity!!.lower_limit
            //委托价格高于涨停价
            if(!Omits.isOmit(upLimitPrice) && upLimitPrice.toDouble() < orderPrice.toDouble()){
                throw IllegalArgumentException(resources.getString(R.string.notice_price_great_than_lower_limit))
            }
            //委托价格低于跌停价
            if(!Omits.isOmit(lowLimit) && lowLimit.toDouble() > orderPrice.toDouble()){
                throw IllegalArgumentException(resources.getString(R.string.notice_price_less_than_lower_limit))
            }
        }
    }

    @Throws(IllegalArgumentException::class)
    open fun checkOrderVolume(volume:String?){
        try{
            if(volume == null || volume.toInt() < 0){
                throw IllegalArgumentException(resources.getString(R.string.notice_volume_error))
            }
        }catch (e:NumberFormatException){
            throw IllegalArgumentException(resources.getString(R.string.notice_volume_error))
        }
    }

    override fun performClick(): Boolean {
        val currentTime = System.currentTimeMillis()
        if(!Omits.isOmit(lastClickTime) && currentTime - lastClickTime < 500){
            return false
        }else{
            lastClickTime = currentTime
        }
        return super.performClick()
    }

    fun performOrderInsert(volume:String?):IOrderInsertField{
        checkOrderParams(volume)
        return createOrderField(volume!!.toInt())
    }

    fun createOrderField(volume:Int): IOrderInsertField {
        val tradeAPI = TradeApiProvider.providerCTPTradeApi()
        val currentUser = tradeAPI.getCurrentUser()!!
        val field = CTPOrderInsertField(currentUser.brokerID,currentUser.userID,orderInstrument!!.ctpInstrumentId,
            tradeAPI.getOrderRefId().toString(),currentUser.userID,CTPOrderPriceType.LimitPrice,direction,combOffset,
            CTPHedgeType.Speculation,orderPrice.toDouble(),volume,
            CTPTimeConditionType.IOC,Omits.OmitString,CTPVolumeConditionType.AV,1,
            CTPContingentConditionType.Immediately,null,CTPForceCloseReasonType.NotForceClose,0,null,
            tradeAPI.getOrderReqId(),0,0,orderInstrument!!.eid,null,currentUser.userID,null,
            null,null,null)
        return field
    }

    fun setOrderDirection(dir:CTPDirection){
        direction = dir
    }

    fun setOrderCombOffset(offset:CTPCombOffsetFlag){
        combOffset = offset
    }

    fun setTransactionInfo(ins:InstrumentInfo?,pType:SupportTransactionOrderPrice,dir:CTPDirection?=null,offset:CTPCombOffsetFlag = CTPCombOffsetFlag.Open){
        orderInstrument = ins
        priceType = pType
        if(dir != null){
            direction = dir
        }
        combOffset = offset
    }
}