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

/**
 * 买卖方向
 */
enum class CTPDirection(val direction: Char, val text: String) {
    Buy('0', "买"),
    Sell('1', "卖");

    companion object {
        private val all: HashMap<Char, CTPDirection> = HashMap()

        init {
            for (dir in values()) {
                all[dir.direction] = dir
            }
        }

        fun from(dir: Char): CTPDirection? {
            return all[dir]
        }
    }
}

/**
 * 投机套保标志
 */
enum class CTPHedgeType(val code: Char, val text: String) {
    Speculation('1', "投机"),
    Arbitrage('2', "套利"),
    Hedge('3', "套保");

    companion object {
        private val all: HashMap<Char, CTPHedgeType> = HashMap()

        init {
            for (hedge in values()) {
                all[hedge.code] = hedge
            }
        }

        fun from(hedge: Char): CTPHedgeType? {
            return all[hedge]
        }
    }
}

/**
 * 价格类型
 */
enum class CTPOrderPriceType(val code: Char, val text: String) {
    ///市价
    AnyPrice('1', "市价"),
    ///限价/条件单
    LimitPrice('2', "限价"),
    ///最优价
    BestPrice('3', "最优价"),
    ///最新价
    LastPrice('4', "最新价"),
    ///最新价浮动上浮1个ticks
    LastPricePlusOneTicks('5', "最新价浮动上浮1个ticks"),
    ///最新价浮动上浮2个ticks
    LastPricePlusTwoTicks('6', "最新价浮动上浮2个ticks"),
    ///最新价浮动上浮3个ticks
    LastPricePlusThreeTicks('7', "最新价浮动上浮3个ticks"),
    ///卖一价
    AskPrice1('8', "卖一价"),
    ///卖一价浮动上浮1个ticks
    AskPrice1PlusOneTicks('9', "卖一价浮动上浮1个ticks"),
    ///卖一价浮动上浮2个ticks
    AskPrice1PlusTwoTicks('A', "卖一价浮动上浮2个ticks"),
    ///卖一价浮动上浮3个ticks
    AskPrice1PlusThreeTicks('B', "卖一价浮动上浮3个ticks"),
    ///买一价
    BidPrice1('C', "买一价"),
    ///买一价浮动上浮1个ticks
    BidPrice1PlusOneTicks('D', "买一价浮动上浮1个ticks"),
    ///买一价浮动上浮2个ticks
    BidPrice1PlusTwoTicks('E', "买一价浮动上浮2个ticks"),
    ///买一价浮动上浮3个ticks
    BidPrice1PlusThreeTicks('F', "买一价浮动上浮3个ticks");

    companion object {
        private val all: HashMap<Char, CTPOrderPriceType> = HashMap()

        init {
            for (type in values()) {
                all[type.code] = type
            }
        }

        fun from(type: Char): CTPOrderPriceType? {
            return all[type]
        }
    }
}

enum class CTPTimeConditionType(val code: Char, val text: String) {

    ///立即完成，否则撤销   
    IOC('1', "立即完成，否则撤销"),//市价
    ///本节有效
    GFS('2', "本节有效"),
    ///当日有效                    
    GFD('3', "当日有效"),//限价、条件单
    ///指定日期前有效
    GTD('4', "指定日期前有效"),
    ///撤销前有效
    GTC('5', "撤销前有效"),
    ///集合竞价有效
    GFA('6', "集合竞价有效");

    companion object {
        private val all: HashMap<Char, CTPTimeConditionType> = HashMap()

        init {
            for (type in values()) {
                all[type.code] = type
            }
        }

        fun from(type: Char): CTPTimeConditionType? {
            return all[type]
        }
    }
}

enum class CTPVolumeConditionType(val code: Char, val text: String) {
    ///任何数量
    AV('1', "任何数量"),//普遍用这个
    ///最小数量
    MV('2', "最小数量"),
    ///全部数量
    CV('3', "全部数量");

    companion object {
        private val all: HashMap<Char, CTPVolumeConditionType> = HashMap()

        init {
            for (type in values()) {
                all[type.code] = type
            }
        }

        fun from(type: Char): CTPVolumeConditionType? {
            return all[type]
        }
    }
}

enum class CTPContingentConditionType(val code: Char, val text: String) {
    ///立即
    Immediately('1', "立即"),
    ///止损
    Touch('2', "止损"),
    ///止赢
    TouchProfit('3', "止赢"),
    ///预埋单
    ParkedOrder('4', "预埋单"),
    ///最新价大于条件价
    LastPriceGreaterThanStopPrice('5', "最新价大于条件价"),
    ///最新价大于等于条件价
    LastPriceGreaterEqualStopPrice('6', "最新价大于等于条件价"),
    ///最新价小于条件价
    LastPriceLesserThanStopPrice('7', "最新价小于条件价"),
    ///最新价小于等于条件价
    LastPriceLesserEqualStopPrice('8', "最新价小于等于条件价"),
    ///卖一价大于条件价
    AskPriceGreaterThanStopPrice('9', "卖一价大于条件价"),
    ///卖一价大于等于条件价
    AskPriceGreaterEqualStopPrice('A', "卖一价大于等于条件价"),
    ///卖一价小于条件价
    AskPriceLesserThanStopPrice('B', "卖一价小于条件价"),
    ///卖一价小于等于条件价
    AskPriceLesserEqualStopPrice('C', "卖一价小于等于条件价"),
    ///买一价大于条件价
    BidPriceGreaterThanStopPrice('D', "买一价大于条件价"),
    ///买一价大于等于条件价
    BidPriceGreaterEqualStopPrice('E', "买一价大于等于条件价"),
    ///买一价小于条件价
    BidPriceLesserThanStopPrice('F', "买一价小于条件价"),
    ///买一价小于等于条件价
    BidPriceLesserEqualStopPrice('H', "买一价小于等于条件价");

    companion object {
        private val all: HashMap<Char, CTPContingentConditionType> = HashMap()

        init {
            for (type in values()) {
                all[type.code] = type
            }
        }

        fun from(type: Char): CTPContingentConditionType? {
            return all[type]
        }
    }
}

enum class CTPForceCloseReasonType(val code: Char, val text: String) {
    ///非强平
    NotForceClose('0', "非强平"),//正常交易选这个
    ///资金不足
    LackDeposit('1', "资金不足"),
    ///客户超仓
    ClientOverPositionLimit('2', "客户超仓"),
    ///会员超仓
    MemberOverPositionLimit('3', "会员超仓"),
    ///持仓非整数倍
    NotMultiple('4', "持仓非整数倍"),
    ///违规
    Violation('5', "违规"),
    ///其它
    Other('6', "其它"),
    ///自然人临近交割
    PersonDeliv('7', "自然人临近交割");

    companion object {
        private val all: HashMap<Char, CTPForceCloseReasonType> = HashMap()

        init {
            for (type in values()) {
                all[type.code] = type
            }
        }

        fun from(type: Char): CTPForceCloseReasonType? {
            return all[type]
        }
    }
}


enum class ExchangeType(
    val exchangeID: String,
    val exchangeName: String,
    val clazzType: Class<out InstrumentPosition>
) {
    CFFEX("CFFEX", "中金所", StandardInstrumentPosition::class.java),
    DCE("DCE", "大商所", StandardInstrumentPosition::class.java),
    CZCE("CZCE", "郑商所", CZCEInstrumentPosition::class.java),
    SHFE("SHFE", "上期所", YTDInstrumentPosition::class.java),
    INE("INE", "能源所", YTDInstrumentPosition::class.java);

    fun getInstrumentInstance(): InstrumentPosition {
        return clazzType.newInstance()
    }

    companion object {
        private val exchanges: HashMap<String, ExchangeType> = HashMap(10)

        init {
            for (exchange in values()) {
                exchanges[exchange.exchangeID] = exchange
            }
        }

        fun from(exchangeID: String?): ExchangeType? = exchanges[exchangeID]
    }
}