package com.future.trade.widget.order

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.fsh.common.model.InstrumentInfo
import com.fsh.common.util.ARouterUtils
import com.fsh.common.util.Omits
import com.fsh.common.util.SizeUtils
import com.future.trade.R
import com.future.trade.bean.CTPOrderInsertField
import com.future.trade.bean.IOrderInsertField
import com.future.trade.enums.*
import com.future.trade.model.SupportTransactionOrderPrice
import com.future.trade.repository.TradeApiProvider
import java.lang.IllegalArgumentException


abstract class OrderButton : View {
    private val textPaint:Paint
    protected var textOrderPrice:String = Omits.OmitPrice
    private var buttonName:String = Omits.OmitString
    private var lastClickTime:Long = Omits.OmitLong
    protected var orderInstrument:InstrumentInfo? = null
    protected var combOffset:CTPCombOffsetFlag
    protected var direction:CTPDirection
    protected var priceType:SupportTransactionOrderPrice = SupportTransactionOrderPrice.Opponent
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    @SuppressLint("ResourceType")
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {

        val typedArray = context?.obtainStyledAttributes(attrs, R.styleable.OrderButton)
        textOrderPrice =
            typedArray?.getString(R.styleable.OrderButton_orderPrice) ?: Omits.OmitPrice
        buttonName = typedArray?.getString(R.styleable.OrderButton_orderText) ?: Omits.OmitString
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
        textPaint.getTextBounds(textOrderPrice,0,textOrderPrice.length,orderPriceBounds)
        val fontMetrics = textPaint.fontMetrics
        val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        val baseline = measuredHeight/4.0f + distance
        canvas?.drawText(textOrderPrice,((measuredWidth - orderPriceBounds.width())/2.0f),
            baseline,textPaint)
        //第二行的文字
        val orderTextBounds = Rect()
        textPaint.getTextBounds(buttonName,0,buttonName.length,orderTextBounds)
        val baseline2 = measuredHeight/4.0f*3.0f + distance
        canvas?.drawText(buttonName,0,buttonName.length,(measuredWidth - orderTextBounds.width())/2.0f,baseline2,textPaint)
    }
    //TODO 这里如果是非限价价格，那么就是文字，不是数字，需要把数字设置过来 price
    fun setOrderPriceText(buttonPrice:String,priceType:SupportTransactionOrderPrice){
        textOrderPrice = buttonPrice
        this.priceType = priceType
        postInvalidate()
    }

    /**
     * 检查报单参数
     */
    @Throws(IllegalArgumentException::class)
    open fun checkOrderParams(volume:String?){
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
        if(Omits.isOmit(textOrderPrice) || textOrderPrice.toDouble() < 0){
            throw IllegalArgumentException(resources.getString(R.string.notice_price_error))
        }
        val exchange = ExchangeType.from(orderInstrument?.instrumentID)
        if(priceType == SupportTransactionOrderPrice.Market){
            //市价单价格不对
            if(exchange != ExchangeType.SHFE && exchange != ExchangeType.INE && textOrderPrice.toDouble() != 0.0){
                throw IllegalArgumentException(resources.getString(R.string.notice_market_price_fail))
            }
        }
        val quoteEntity = ARouterUtils.getQuoteService().getQuoteByInstrument(orderInstrument?.instrumentID)
        if(quoteEntity != null){
            val upLimitPrice = quoteEntity!!.upper_limit
            val lowLimit = quoteEntity!!.lower_limit
            //委托价格高于涨停价
            if(!Omits.isOmit(upLimitPrice) && upLimitPrice.toDouble() < textOrderPrice.toDouble()){
                throw IllegalArgumentException(resources.getString(R.string.notice_price_great_than_lower_limit))
            }
            //委托价格低于跌停价
            if(!Omits.isOmit(lowLimit) && lowLimit.toDouble() > textOrderPrice.toDouble()){
                throw IllegalArgumentException(resources.getString(R.string.notice_price_less_than_lower_limit))
            }
        }
    }

    @Throws(IllegalArgumentException::class)
    open fun checkOrderVolume(volume:String?){
        require(volume != null && volume.toInt() >0){resources.getString(R.string.notice_volume_error)}
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

    fun performOrderInsert(volume:String?):List<IOrderInsertField>{
        checkOrderParams(volume)
        return createOrderField(volume!!.toInt())
    }

    open fun createOrderField(volume:Int): List<IOrderInsertField> {
        val tradeAPI = TradeApiProvider.providerCTPTradeApi()
        val currentUser = tradeAPI.getCurrentUser()!!
        return listOf(CTPOrderInsertField(currentUser.brokerID,currentUser.userID,
            orderInstrument!!.ctpInstrumentId,
            tradeAPI.getOrderRefId().toString(),currentUser.userID,CTPOrderPriceType.LimitPrice,
            direction,
            combOffset,
            CTPHedgeType.Speculation,
            textOrderPrice.toDouble(),volume,
            CTPTimeConditionType.IOC,Omits.OmitString,CTPVolumeConditionType.AV,1,
            CTPContingentConditionType.Immediately,null,CTPForceCloseReasonType.NotForceClose,0,null,
            tradeAPI.getOrderReqId(),0,0,
            orderInstrument!!.exchangeID,null,currentUser.userID,null,
            null,null,null))
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