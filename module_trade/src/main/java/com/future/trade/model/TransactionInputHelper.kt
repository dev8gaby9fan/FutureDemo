package com.future.trade.model

import android.text.TextUtils
import android.widget.TextView
import androidx.core.text.isDigitsOnly
import com.fsh.common.model.InstrumentInfo
import com.fsh.common.model.QuoteEntity
import com.fsh.common.provider.QuoteService
import com.fsh.common.util.ARouterUtils
import com.fsh.common.util.NumberUtils
import com.fsh.common.util.Omits
import com.future.trade.enums.CTPDirection
import com.future.trade.enums.ExchangeType
import com.future.trade.util.VerifyUtil
import com.future.trade.widget.keyboard.FutureKeyboard
import com.future.trade.widget.keyboard.SimpleFutureKeyboardListener
import java.math.BigDecimal
import java.util.regex.Pattern
import kotlin.math.max

class TransactionInputHelper(private val priceInput: TextView, private val volumeInput: TextView) :
    SimpleFutureKeyboardListener() {
    companion object {
        const val DEFAULT_ORDRE_VOLUME = 1
        const val PRICE_MAX_VALUE_LEN = 9
        const val VOLUME_MAX_VALUE_LEN = 4
        private const val PATTERN_NUM = "([1-9]\\d*\\.\\d*)|(0\\.\\d*[1-9]\\d*)|([1-9]\\d*)"
        val NUM_ONE = BigDecimal("1").toDouble()
        val NUM_N_ONE = BigDecimal("-1").toDouble()
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

    fun setTradeInstrument(ins: InstrumentInfo) {
        this.instrument = ins
        this.tradeVolume = DEFAULT_ORDRE_VOLUME
        tradePrice = Omits.OmitPrice
        updateTradePrice(quoteService?.getQuoteByInstrument(ins.shortInsId))
        priceInput.text = tradePrice
        volumeInput.text = tradeVolume.toString()
    }

    fun updateTradePrice(quoteEntity: QuoteEntity?) {
        if (quoteEntity == null) {
            return
        }
        //限价
        tradePrice = if (priceType == SupportTransactionOrderPrice.Limit) {
            NumberUtils.formatNum(quoteEntity.last_price, instrument?.priceTick)
        } else {
            priceType.text
        }
        priceInput.post {
            priceInput.text = tradePrice
        }
    }

    fun changePriceType(type: SupportTransactionOrderPrice) {
        if (instrument == null) {
            return
        }
        priceType = type
        updateTradePrice(quoteService?.getQuoteByInstrument(instrument?.shortInsId))
        priceInput.post {
            priceInput.text = tradePrice
        }
    }

    fun changeTradeVolume(volume: Int) {
        tradeVolume = volume
        volumeInput.text = tradeVolume.toString()
    }

    /**
     * 获取委托价格
     *   根据开平方向和价格模式
     */
    fun getOrderPrice(direction: CTPDirection): Double {
        val quoteEntity = quoteService?.getQuoteByInstrument(instrument?.shortInsId)
        return when (priceType) {
            SupportTransactionOrderPrice.Queue -> {
                val upLimitPrice: String =
                    NumberUtils.formatNum(quoteEntity?.upper_limit, instrument?.priceTick)
                val lowLimitPrice: String =
                    NumberUtils.formatNum(quoteEntity?.last_price, instrument?.priceTick)
                val lastPrice: String =
                    NumberUtils.formatNum(quoteEntity?.last_price, instrument?.priceTick)
                // 买方向用卖价，卖方向用买价 涨停无卖价，直接就使用涨停价
                if ((lastPrice == upLimitPrice && !Omits.isOmit(quoteEntity?.last_price)) || (lastPrice == lowLimitPrice && !Omits.isOmit(
                        quoteEntity?.last_price
                    ))
                ) {
                    lastPrice.toDouble()
                } else {
                    val askPrice =
                        NumberUtils.formatNum(quoteEntity?.ask_price1, instrument?.priceTick)
                    val bidPrice =
                        NumberUtils.formatNum(quoteEntity?.bid_price1, instrument?.priceTick)
                    if (direction == CTPDirection.Buy) {
                        if (Omits.isOmit(bidPrice)) 0.0 else bidPrice.toDouble()
                    } else {
                        if (Omits.isOmit(askPrice)) 0.0 else askPrice.toDouble()
                    }
                }
            }
            //对手价
            SupportTransactionOrderPrice.Opponent -> {
                val upLimitPrice: String =
                    NumberUtils.formatNum(quoteEntity?.upper_limit, instrument?.priceTick)
                val lowLimitPrice: String =
                    NumberUtils.formatNum(quoteEntity?.last_price, instrument?.priceTick)
                val lastPrice: String =
                    NumberUtils.formatNum(quoteEntity?.last_price, instrument?.priceTick)
                // 买方向用卖价，卖方向用买价 涨停无卖价，直接就使用涨停价
                if ((lastPrice == upLimitPrice && !Omits.isOmit(quoteEntity?.last_price)) || (lastPrice == lowLimitPrice && !Omits.isOmit(
                        quoteEntity?.last_price
                    ))
                ) {
                    lastPrice.toDouble()
                } else {
                    val askPrice =
                        NumberUtils.formatNum(quoteEntity?.ask_price1, instrument?.priceTick)
                    val bidPrice =
                        NumberUtils.formatNum(quoteEntity?.bid_price1, instrument?.priceTick)
                    if (direction == CTPDirection.Buy) {
                        if (Omits.isOmit(askPrice)) 0.0 else askPrice.toDouble()
                    } else {
                        if (Omits.isOmit(bidPrice)) 0.0 else bidPrice.toDouble()
                    }
                }
            }
            SupportTransactionOrderPrice.Market -> {
                val type = ExchangeType.from(instrument?.eid) ?: return 0.0
                if (quoteEntity == null || Omits.isOmit(quoteEntity.highest)
                    || Omits.isOmit(quoteEntity.lower_limit)
                ) {
                    0.0
                }
                //上期能源所不支持市价单，用涨跌停去搞
                if (type == ExchangeType.INE || type == ExchangeType.SHFE) {
                    //没有获取到涨跌停，就搞一个0
                    if (Omits.isOmit(quoteEntity?.upper_limit) || Omits.isOmit(quoteEntity?.lower_limit)) {
                        0.0
                    }
                    if (direction == CTPDirection.Buy) quoteEntity!!.upper_limit.toDouble() else quoteEntity!!.lower_limit.toDouble()
                } else {
                    0.0
                }
            }
            SupportTransactionOrderPrice.Limit -> {
                if (Omits.isOmit(tradePrice) || !numPattern.matcher(tradePrice).matches()) {
                    if (quoteEntity == null || Omits.isOmit(quoteEntity?.last_price)) {
                        0.0
                    } else {
                        quoteEntity.last_price.toDouble()
                    }
                } else {
                    tradePrice.toDouble()
                }
            }
        }
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
            handleNumberInput(num, priceInput, PRICE_MAX_VALUE_LEN)
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
            handleAddInput(priceInput, NUM_ONE, PRICE_MAX_VALUE_LEN, NUM_ONE)
        } else {
            handleAddInput(volumeInput, NUM_ONE, VOLUME_MAX_VALUE_LEN, NUM_ONE)
        }
    }

    private fun handleAddInput(
        inputField: TextView,
        addValue: Double,
        maxLen: Int,
        formatNum: Double
    ) {
        if (Omits.isOmit(inputField.text?.toString()) || (!numPattern.matcher(inputField.text).matches() && "0" != inputField.text?.toString())) {
            inputField.text = NumberUtils.formatNum(addValue, formatNum)
        } else {

            var addResult = NumberUtils.add(addValue.toString(), inputField.text.toString())
            addResult = NumberUtils.formatNum(addResult, formatNum.toString())
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
            handleAddInput(priceInput, NUM_N_ONE, PRICE_MAX_VALUE_LEN, NUM_ONE)
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
            handleDeleteInput(priceInput)
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
}

enum class SupportTransactionOrderPrice(val text: String) {
    Queue("排队价"),
    Opponent("对手价"),
    Market("市价"),
    Limit("限价"),
}