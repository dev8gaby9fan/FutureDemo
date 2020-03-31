package com.fsh.trade.model

import android.widget.TextView
import androidx.core.text.isDigitsOnly
import com.fsh.common.model.InstrumentInfo
import com.fsh.common.model.QuoteEntity
import com.fsh.common.provider.QuoteService
import com.fsh.common.util.ARouterUtils
import com.fsh.common.util.NumberUtils
import com.fsh.common.util.Omits
import com.fsh.trade.enums.CTPDirection
import com.fsh.trade.enums.ExchangeType

class TransactionInputHelper(private val priceInput:TextView,private val volumeInput:TextView) {
    private var instrument:InstrumentInfo? = null
    //交易输入框委托数据
    private var tradeVolume:Int = 0
    //委托价格
    private var tradePrice:String = Omits.OmitPrice
    //默认对手价
    private var priceType:SupportTransactionOrderPrice = SupportTransactionOrderPrice.Opponent
    private val quoteService:QuoteService? = ARouterUtils.getQuoteService()

    fun setTradeInstrument(ins:InstrumentInfo){
        this.instrument = ins
        this.tradeVolume = DEFAULT_ORDRE_VOLUME
        tradePrice = Omits.OmitPrice
        updateTradePrice(quoteService?.getQuoteByInstrument(ins.shortInsId))
        priceInput.text = tradePrice
        volumeInput.text = tradeVolume.toString()
    }

    fun updateTradePrice(quoteEntity:QuoteEntity?){
        if(quoteEntity == null){
            return
        }
        //限价
        tradePrice = if(priceType == SupportTransactionOrderPrice.Limit){
            NumberUtils.formatNum(quoteEntity.last_price,instrument?.priceTick)
        }else{
            priceType.text
        }
        priceInput.post {
            priceInput.text = tradePrice
        }
    }

    fun changePriceType(type:SupportTransactionOrderPrice){
        if(instrument == null){
            return
        }
        priceType = type
        updateTradePrice(quoteService?.getQuoteByInstrument(instrument?.shortInsId))
        priceInput.post {
            priceInput.text = tradePrice
        }
    }

    fun changeTradeVolume(volume:Int){
        tradeVolume = volume
        volumeInput.text = tradeVolume.toString()
    }

    /**
     * 获取委托价格
     *   根据开平方向和价格模式
     */
    fun getOrderPrice(direction:CTPDirection):Double{
        val quoteEntity = quoteService?.getQuoteByInstrument(instrument?.shortInsId)
        return when(priceType){
            SupportTransactionOrderPrice.Queue -> {
                val upLimitPrice:String = NumberUtils.formatNum(quoteEntity?.upper_limit,instrument?.priceTick)
                val lowLimitPrice:String = NumberUtils.formatNum(quoteEntity?.last_price,instrument?.priceTick)
                val lastPrice:String = NumberUtils.formatNum(quoteEntity?.last_price,instrument?.priceTick)
                // 买方向用卖价，卖方向用买价 涨停无卖价，直接就使用涨停价
                if((lastPrice == upLimitPrice && !Omits.isOmit(quoteEntity?.last_price)) || (lastPrice == lowLimitPrice && !Omits.isOmit(quoteEntity?.last_price))){
                    lastPrice.toDouble()
                }else{
                    val askPrice = NumberUtils.formatNum(quoteEntity?.ask_price1,instrument?.priceTick)
                    val bidPrice = NumberUtils.formatNum(quoteEntity?.bid_price1,instrument?.priceTick)
                    if(direction == CTPDirection.Buy){
                        if(Omits.isOmit(bidPrice)) 0.0 else bidPrice.toDouble()
                    }else{
                        if(Omits.isOmit(askPrice)) 0.0 else askPrice.toDouble()
                    }
                }
            }
            //对手价
            SupportTransactionOrderPrice.Opponent -> {
                val upLimitPrice:String = NumberUtils.formatNum(quoteEntity?.upper_limit,instrument?.priceTick)
                val lowLimitPrice:String = NumberUtils.formatNum(quoteEntity?.last_price,instrument?.priceTick)
                val lastPrice:String = NumberUtils.formatNum(quoteEntity?.last_price,instrument?.priceTick)
                // 买方向用卖价，卖方向用买价 涨停无卖价，直接就使用涨停价
                if((lastPrice == upLimitPrice && !Omits.isOmit(quoteEntity?.last_price)) || (lastPrice == lowLimitPrice && !Omits.isOmit(quoteEntity?.last_price))){
                    lastPrice.toDouble()
                }else{
                    val askPrice = NumberUtils.formatNum(quoteEntity?.ask_price1,instrument?.priceTick)
                    val bidPrice = NumberUtils.formatNum(quoteEntity?.bid_price1,instrument?.priceTick)
                    if(direction == CTPDirection.Buy){
                        if(Omits.isOmit(askPrice)) 0.0 else askPrice.toDouble()
                    }else{
                        if(Omits.isOmit(bidPrice)) 0.0 else bidPrice.toDouble()
                    }
                }
            }
            SupportTransactionOrderPrice.Market -> {
                val type = ExchangeType.from(instrument?.eid) ?: return 0.0
                if(quoteEntity == null || Omits.isOmit(quoteEntity.highest)
                    || Omits.isOmit(quoteEntity.lower_limit)){
                    0.0
                }
                //上期能源所不支持市价单，用涨跌停去搞
                if(type == ExchangeType.INE || type == ExchangeType.SHFE){
                    //没有获取到涨跌停，就搞一个0
                    if(Omits.isOmit(quoteEntity?.upper_limit) || Omits.isOmit(quoteEntity?.lower_limit)){
                        0.0
                    }
                    if(direction == CTPDirection.Buy) quoteEntity!!.upper_limit.toDouble() else quoteEntity!!.lower_limit.toDouble()
                }else{
                    0.0
                }
            }
            SupportTransactionOrderPrice.Limit->{
                if(Omits.isOmit(tradePrice) || !tradePrice.isDigitsOnly()){
                    if(quoteEntity == null || Omits.isOmit(quoteEntity?.last_price)){
                        0.0
                    }else{
                        quoteEntity.last_price.toDouble()
                    }
                }else{
                    tradePrice.toDouble()
                }
            }
        }
    }

    fun getOrderVolume():Int = tradeVolume

    fun getTradeInstrument():InstrumentInfo? = instrument

    companion object{
        const val DEFAULT_ORDRE_VOLUME = 1
    }
}

enum class SupportTransactionOrderPrice(val text:String){
    Queue("排队价"),
    Opponent("对手价"),
    Market("市价"),
    Limit("限价"),
}