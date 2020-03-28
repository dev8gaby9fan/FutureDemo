package com.fsh.trade.enums

/**
 * 报单状态
 * @param codeInt 状态码
 * @param notion 定义
 * @param remark 备注
 */
enum class CTPOrderStatusType(val code:Char,val notion:String,val remark:String,val isOver:Boolean) {
    Fill('0',"全部成交","已全部成交",true),
    PART_FILL_IN_QUEUE('1',"部分成交还在队列","部分成交，剩余部分在等待成交",false),
    PART_FILL_NOT_IN_QUEUE('2',"部分成交不在队列","部分成交，剩余部分已撤单",true),
    IN_QUEUE('3',"未成交还在队列中","报单已发送往交易所正在等待成交",false),
    NOT_IN_QUEUE('4',"未成交不在队列","报单还未发往交易所",false),
    ACTION('5',"撤单","已全部撤单",true),
    Unkonwn('a',"未知","--",false),
    NOT_EXECUTE('b',"尚未触发","预埋单等未达到触发下单条件，客户端还未执行下单动作",false),
    EXECUTED('c',"已触发","预埋单等已达到触发下单条件，客户端执行下单动作",true);

    companion object{
        private val allStatus:HashMap<Char,CTPOrderStatusType> = HashMap()
        init {
            for(status in values()){
                allStatus[status.code] = status
            }
        }

        fun from(code:Char):CTPOrderStatusType{
            return if(allStatus.containsKey(code)){
                allStatus[code]!!
            }else{
                Unkonwn
            }
        }
    }
}