package com.fsh.trade.util

import android.util.Patterns
import java.lang.IllegalArgumentException
import java.util.regex.Pattern

class VerifyUtil {
    companion object{
        val instance:VerifyUtil by lazy{
            VerifyUtil()
        }
        //经纪公司ID
        private const val PATTERN_NUMBER = "[0-9]{4,8}"
        //交易账号
        private const val PATTERN_TRADE_ACC = "[0-9]{6,18}"

        private const val PATTERN_IP_ADD =
            ("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9])):[1-9]\\d+")
        private const val PATTERN_URL_ADD = ""
        private const val PATTERN_PASS_WORD = "[0-9A-Za-z]{6,18}"
    }

    fun isBrokerID(input:CharSequence):VerifyUtil{
        require(Pattern.matches(PATTERN_NUMBER,input)) { "请输入4-8位的数字编号" }
        return this
    }

    fun isTradeAccount(input:CharSequence):VerifyUtil{
        require(Pattern.matches(PATTERN_TRADE_ACC,input)) { "请输入6-18位的数字账号" }
        return this
    }

    fun isPassword(input:CharSequence):VerifyUtil{
        require(Pattern.matches(PATTERN_PASS_WORD,input)){"请输入6-18位数字和字母密码"}
        return this
    }

    fun isFrontIP(input:CharSequence):VerifyUtil{
        Patterns.WEB_URL
        require(Pattern.matches(PATTERN_IP_ADD,input)){"请输入正确的交易地址"}
        return this
    }

    fun isBlank(input:CharSequence?,errorMsg:String="请输入内容"):VerifyUtil{
        require(input != null && input.isNotEmpty()){errorMsg}
        return this
    }

    fun isNotNull(obj:Any?,errorMsg: String):VerifyUtil{
        require(obj != null){errorMsg}
        return this
    }

}