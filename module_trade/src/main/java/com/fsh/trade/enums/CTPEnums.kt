package com.fsh.trade.enums

import com.fsh.trade.bean.CZCEInstrumentPosition
import com.fsh.trade.bean.InstrumentPosition
import com.fsh.trade.bean.StandardInstrumentPosition
import com.fsh.trade.bean.YTDInstrumentPosition

/**
 * 报单状态
 * @param code 状态码
 * @param notion 定义
 * @param remark 备注
 */
enum class CTPOrderStatusType(
    val code: Char,
    val notion: String,
    val remark: String,
    val isOver: Boolean
) {
    Fill('0', "全部成交", "已全部成交", true),
    PART_FILL_IN_QUEUE('1', "部分成交还在队列", "部分成交，剩余部分在等待成交", false),
    PART_FILL_NOT_IN_QUEUE('2', "部分成交不在队列", "部分成交，剩余部分已撤单", true),
    IN_QUEUE('3', "未成交还在队列中", "报单已发送往交易所正在等待成交", false),
    NOT_IN_QUEUE('4', "未成交不在队列", "报单还未发往交易所", false),
    ACTION('5', "撤单", "已全部撤单", true),
    Unkonwn('a', "未知", "--", false),
    NOT_EXECUTE('b', "尚未触发", "预埋单等未达到触发下单条件，客户端还未执行下单动作", false),
    EXECUTED('c', "已触发", "预埋单等已达到触发下单条件，客户端执行下单动作", true);

    companion object {
        private val allStatus: HashMap<Char, CTPOrderStatusType> = HashMap()

        init {
            for (status in values()) {
                allStatus[status.code] = status
            }
        }

        fun from(code: Char): CTPOrderStatusType {
            return if (allStatus.containsKey(code)) {
                allStatus[code]!!
            } else {
                Unkonwn
            }
        }
    }
}

/**
 * 开平标志位
 *  上期\能源所，   平今仓只可用closeToday，平昨仓可用close或closeYesterday
 *  非上期\能源所，平今仓可用close或closeToday，平昨仓可用close或closeYesterday。
 */
enum class CTPCombOffsetFlag(val offset: Char, val text: String) {
    Open('0', "开"),
    Close('1', "平"),
    CloseToday('2', "平今"),
    CloseYesterday('3', "平昨"),
    ForceClose('4', "强平"),
    LocalForceClose('5', "本地强平");

    companion object {
        private val allOffset: HashMap<Char, CTPCombOffsetFlag> = HashMap(10)

        init {
            for (offset in values()) {
                allOffset[offset.offset] = offset
            }
        }

        fun from(offset: Char): CTPCombOffsetFlag {
            if (allOffset.containsKey(offset)) {
                return allOffset[offset]!!
            }
            return Open
        }
    }
}

enum class ExchangeType(val exchangeID: String, val exchangeName: String,val clazzType:Class<out InstrumentPosition>) {
    CFFEX("CFFEX", "中金所",StandardInstrumentPosition::class.java),
    DCE("DCE", "大商所",StandardInstrumentPosition::class.java),
    CZCE("CZCE", "郑商所",CZCEInstrumentPosition::class.java),
    SHFE("SHFE", "上期所",YTDInstrumentPosition::class.java),
    INE("INE", "能源所",YTDInstrumentPosition::class.java);

    fun getInstrumentInstance():InstrumentPosition{
        return clazzType.newInstance()
    }

    companion object {
        private val exchanges: HashMap<String, ExchangeType> = HashMap(10)

        init {
            for (exchange in values()) {
                exchanges[exchange.exchangeID] = exchange
            }
        }

        fun from(exchangeID: String): ExchangeType? = exchanges[exchangeID]
    }
}