package com.future.trade.model

import android.text.TextUtils
import android.widget.TextView
import com.fsh.common.model.InstrumentInfo
import com.fsh.common.model.QuoteEntity
import com.fsh.common.provider.QuoteService
import com.fsh.common.util.ARouterUtils
import com.fsh.common.util.NumberUtils
import com.fsh.common.util.Omits
import com.future.trade.bean.position.Position
import com.future.trade.enums.ExchangeType
import com.future.trade.repository.TradeApiProvider
import com.future.trade.widget.keyboard.FutureKeyboard
import com.future.trade.widget.keyboard.SimpleFutureKeyboardListener
import com.future.trade.widget.order.ClosePositionOrderButton
import com.future.trade.widget.order.OrderButton
import java.math.BigDecimal
import java.util.regex.Pattern

class TransactionInputHelper(private val priceInput: TextView, private val volumeInput: TextView,
                             private val buyButton:OrderButton,private val sellButton:OrderButton,
                             private val closeButton:ClosePositionOrderButton) :
    SimpleFutureKeyboardListener() {
    companion object {
        const val DEFAULT_ORDRE_VOLUME = 1
        const val PRICE_MAX_VALUE_LEN = 9
        const val VOLUME_MAX_VALUE_LEN = 4
        private const val PATTERN_NUM = "([1-9]\\d*\\.\\d*)|(0\\.\\d*[1-9]\\d*)|([1-9]\\d*)"
        val NUM_ONE = BigDecimal("1").toString()
        val NUM_N_ONE = BigDecimal("-1").toString()
        val numPattern = Pattern.compile(PATTERN_NUM)
    }

    private var instrument: InstrumentInfo? = null
    //交易输入框委托数据
    private var tradeVolume: Int = 0
    //委托价格
    private var tradePrice: String = Omits.OmitPrice
    //默认对手价
    private var priceType: SupportTransactionOrderPrice = SupportTransactionOrderPrice.Opponent
    private val quoteService: QuoteService? = ARouterUtils.getQuoteService()
    private lateinit var inputType: FutureKeyboard.KeyboardType
    //是否是准备开始输入数据，用于刚设置了输入的类型,新输入的数据从0开始
    private var isBeginInput: Boolean = true

    fun setTradeInstrument(ins: InstrumentInfo,pos: Position? = null) {
        this.instrument = ins
        this.tradeVolume = DEFAULT_ORDRE_VOLUME
        tradePrice = Omits.OmitPrice
        //价格模式默认为对手价
        changePriceType(SupportTransactionOrderPrice.Opponent)
        buyButton.setTransactionInfo(ins,priceType)
        sellButton.setTransactionInfo(ins,priceType)
        var settingPos = pos
        if(settingPos == null){
            settingPos = TradeApiProvider.providerTransactionRepository().getPositionByInstrumentId(ins.ctpInstrumentId)
        }
        closeButton.setTradePosition(settingPos,priceType)
        onQuoteUpdate(quoteService?.getQuoteByInstrument(ins.instrumentID))
        volumeInput.text = tradeVolume.toString()
    }

    fun onQuoteUpdate(quoteEntity: QuoteEntity?){
        setOrderButtonText(quoteEntity)
    }

    fun changePriceType(type: SupportTransactionOrderPrice) {
        priceType = type
        val quote  = quoteService?.getQuoteByInstrument(instrument?.instrumentID)
        tradePrice = if (priceType == SupportTransactionOrderPrice.Limit) {
            if(quote != null){
                NumberUtils.formatNum(quote.last_price, instrument?.priceTick)
            }else{
                "0"
            }
        } else {
            priceType.text
        }
        priceInput.text = tradePrice
        onQuoteUpdate(quote)
    }

    /**
     * 获取委托价格
     *   根据开平方向和价格模式
     */
    private fun setOrderButtonText(quoteEntity: QuoteEntity?) {
        if(quoteEntity == null){
            buyButton.setOrderPriceText(Omits.OmitPrice,priceType)
            sellButton.setOrderPriceText(Omits.OmitPrice,priceType)
            closeButton.setOrderPriceText(Omits.OmitPrice,priceType)
            return
        }
        //买方向的价格
        var buyText = Omits.OmitString
        //卖方向的价格
        var sellText = Omits.OmitPrice
        when (priceType) {
            SupportTransactionOrderPrice.Queue -> {
                val upLimitPrice: String =
                    NumberUtils.formatNum(quoteEntity.upper_limit, instrument?.priceTick)
                val lowLimitPrice: String =
                    NumberUtils.formatNum(quoteEntity.lower_limit, instrument?.priceTick)
                val lastPrice: String =
                    NumberUtils.formatNum(quoteEntity.last_price, instrument?.priceTick)
                // 买方向用卖价，卖方向用买价 涨停无卖价，直接就使用涨停价
                if ((lastPrice == upLimitPrice && !Omits.isOmit(quoteEntity.last_price)) || (lastPrice == lowLimitPrice && !Omits.isOmit(
                        quoteEntity.last_price
                    ))) {
                    buyText = lastPrice
                    sellText = lastPrice
                } else {
                    val askPrice =
                        NumberUtils.formatNum(quoteEntity.ask_price1, instrument?.priceTick)
                    val bidPrice =
                        NumberUtils.formatNum(quoteEntity.bid_price1, instrument?.priceTick)
                    buyText = bidPrice
                    sellText = askPrice
                }
            }
            //对手价
            SupportTransactionOrderPrice.Opponent -> {
                val upLimitPrice: String =
                    NumberUtils.formatNum(quoteEntity.upper_limit, instrument?.priceTick)
                val lowLimitPrice: String =
                    NumberUtils.formatNum(quoteEntity.lower_limit, instrument?.priceTick)
                val lastPrice: String =
                    NumberUtils.formatNum(quoteEntity.last_price, instrument?.priceTick)
                // 买方向用卖价，卖方向用买价 涨停无卖价，直接就使用涨停价
                if ((lastPrice == upLimitPrice && !Omits.isOmit(quoteEntity.last_price)) || (lastPrice == lowLimitPrice && !Omits.isOmit(
                        quoteEntity.last_price
                    ))) {
                    buyText = lastPrice
                    sellText = lastPrice
                } else {
                    val askPrice =
                        NumberUtils.formatNum(quoteEntity.ask_price1, instrument?.priceTick)
                    val bidPrice =
                        NumberUtils.formatNum(quoteEntity.bid_price1, instrument?.priceTick)
                    buyText = askPrice
                    sellText = bidPrice
                }
            }
            SupportTransactionOrderPrice.Market -> {
                val type = ExchangeType.from(instrument?.exchangeID) ?: return
                //上期能源所不支持市价单，用涨跌停去搞
                if (type == ExchangeType.INE || type == ExchangeType.SHFE) {
                    //没有获取到涨跌停，就搞一个0
                    if (Omits.isOmit(quoteEntity.upper_limit) || Omits.isOmit(quoteEntity.lower_limit)) {
                        buyText = "0"
                        sellText = "0"
                    }else{
                        buyText = NumberUtils.formatNum(quoteEntity.upper_limit,instrument?.priceTick)
                        sellText = NumberUtils.formatNum(quoteEntity.lower_limit,instrument?.priceTick)
                    }
                } else {
                    buyText = "0"
                    sellText = "0"
                }
            }
            SupportTransactionOrderPrice.Limit -> {
                if (Omits.isOmit(tradePrice) || !numPattern.matcher(tradePrice).matches()) {
                    if (quoteEntity == null || Omits.isOmit(quoteEntity?.last_price)) {
                        buyText = "0"
                        sellText = "0"
                    } else {
                        buyText = NumberUtils.formatNum(quoteEntity.last_price,instrument?.priceTick)
                        sellText = NumberUtils.formatNum(quoteEntity.last_price,instrument?.priceTick)
                    }
                } else {
                    buyText = tradePrice
                    sellText = tradePrice
                }
            }
        }
        buyButton.setOrderPriceText(buyText,priceType)
        sellButton.setOrderPriceText(sellText,priceType)
        closeButton.setOrderPriceText(buyText,priceType)
    }

    fun getOrderPriceType():SupportTransactionOrderPrice = priceType

    fun getOrderVolume(): String? = volumeInput.text?.toString()

    fun getTradeInstrument(): InstrumentInfo? = instrument


    fun changeInputType(type: FutureKeyboard.KeyboardType) {
        this.inputType = type
    }

    /**
     * 数字按钮输入
     */
    override fun onNumberKeyDown(num: Int) {
        if (inputType == FutureKeyboard.KeyboardType.Price) {
            if(priceType != SupportTransactionOrderPrice.Limit){
                priceType = SupportTransactionOrderPrice.Limit
                isBeginInput = true
            }
            handleNumberInput(num, priceInput, PRICE_MAX_VALUE_LEN)
            updateButtonPriceByInput()
        } else {
            handleNumberInput(num, volumeInput, VOLUME_MAX_VALUE_LEN)
        }
    }

    private fun handleNumberInput(num: Int, inputField: TextView, maxLen: Int) {
        if (isBeginInput || TextUtils.isEmpty(inputField.text) || Omits.isOmit(inputField.text.toString())
            || inputField.text.toString() == "0" || !numPattern.matcher(inputField.text).matches()) {
            inputField.text = num.toString()
            isBeginInput = false
        } else if (inputField.length() < maxLen) {
            inputField.append(num.toString())
        }
    }

    /**
     * 加号输入
     */
    override fun onAddKeyDown() {
        if (inputType == FutureKeyboard.KeyboardType.Price) {
            if(priceType != SupportTransactionOrderPrice.Limit){
                priceType = SupportTransactionOrderPrice.Limit
            }
            val addNum = instrument?.priceTick ?: NUM_ONE
            handleAddInput(priceInput, addNum, PRICE_MAX_VALUE_LEN, addNum)
            updateButtonPriceByInput()
        } else {
            handleAddInput(volumeInput, NUM_ONE, VOLUME_MAX_VALUE_LEN, NUM_ONE)
        }
    }

    private fun handleAddInput(
        inputField: TextView,
        addValue: String,
        maxLen: Int,
        formatNum: String
    ) {
        if (Omits.isOmit(inputField.text?.toString()) || (!numPattern.matcher(inputField.text).matches() && "0" != inputField.text?.toString())) {
            inputField.text = NumberUtils.formatNum(addValue, formatNum)
        } else {

            var addResult = NumberUtils.add(addValue, inputField.text.toString())
            addResult = NumberUtils.formatNum(addResult, formatNum)
            if (addResult.length > maxLen || BigDecimal(addResult).toDouble() <= 0) {
                inputField.text = "0"
            } else {
                inputField.text = addResult
            }
        }
    }

    /**
     * 减号输入
     */
    override fun onSubKeyDown() {
        if (inputType == FutureKeyboard.KeyboardType.Price) {
            if(priceType != SupportTransactionOrderPrice.Limit){
                priceType = SupportTransactionOrderPrice.Limit
            }
            val addNum = BigDecimal(instrument?.priceTick?:NUM_ONE).multiply(BigDecimal(-1)).toString()
            handleAddInput(priceInput, addNum, PRICE_MAX_VALUE_LEN, instrument?.priceTick?:NUM_ONE)
            updateButtonPriceByInput()
        } else {
            handleAddInput(volumeInput, NUM_N_ONE, VOLUME_MAX_VALUE_LEN, NUM_ONE)
        }
    }

    /**
     * 小数点输入
     */
    override fun onDelKeyDown() {
        //如果输入的不是价格，就不处理了
        if (inputType != FutureKeyboard.KeyboardType.Price) {
            return
        }
        if (Omits.isOmit(priceInput.text?.toString()) || priceInput.text.toString() == "0"
            || !numPattern.matcher(priceInput.text).matches()) {
            priceInput.text = "0."
        } else {
            if (!priceInput.text.contains(Regex("\\."))) {
                priceInput.append(".")
            }
        }
    }

    /**
     * 清空按钮输入
     */
    override fun onClearKeyDown() {
        if (inputType == FutureKeyboard.KeyboardType.Volume) {
            volumeInput.text = "0"
            isBeginInput = true
        }
    }

    /**
     * 删除按钮输入
     */
    override fun onDeleteKeyDown() {
        if (inputType == FutureKeyboard.KeyboardType.Price) {
            if(priceType != SupportTransactionOrderPrice.Limit){
                priceType = SupportTransactionOrderPrice.Limit
            }
            handleDeleteInput(priceInput)
            updateButtonPriceByInput()
        } else {
            handleDeleteInput(volumeInput)
        }
    }

    private fun handleDeleteInput(inputField: TextView) {
        //没有内容或者只有一个数值时，直接设置为0
        if (TextUtils.isEmpty(inputField.text) || inputField.length() == 1
            || Omits.isOmit(inputField.text.toString())
            || !numPattern.matcher(priceInput.text).matches()
        ) {
            inputField.text = "0"
            isBeginInput = true
        } else {
            val text = inputField.text.toString()
            inputField.text = text.substring(0, text.length - 1)
        }
    }

    /**
     * 限价按钮输入
     */
    override fun onPriceLimitKeyDown() {
        isBeginInput = false
        changePriceType(SupportTransactionOrderPrice.Limit)
    }

    /**
     * 市场价输入
     */
    override fun onPriceMarketKeyDown() {
        isBeginInput = true
        changePriceType(SupportTransactionOrderPrice.Market)
    }

    /**
     * 对手价输入
     */
    override fun onPriceOpponentKeyDown() {
        isBeginInput = true
        changePriceType(SupportTransactionOrderPrice.Opponent)
    }

    /**
     * 排队价输入
     */
    override fun onPriceQueueKeyDown() {
        isBeginInput = true
        changePriceType(SupportTransactionOrderPrice.Queue)
    }

    private fun updateButtonPriceByInput(){
        val price = priceInput.text.toString()
        tradePrice = price
        buyButton.setOrderPriceText(price,priceType)
        sellButton.setOrderPriceText(price,priceType)
        closeButton.setOrderPriceText(price,priceType)

    }
}

enum class SupportTransactionOrderPrice(val text: String) {
    Queue("排队价"),
    Opponent("对手价"),
    Market("市价"),
    Limit("限价"),
}