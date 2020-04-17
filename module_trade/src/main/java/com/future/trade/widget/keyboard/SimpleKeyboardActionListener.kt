package com.future.trade.widget.keyboard

import android.inputmethodservice.KeyboardView
import android.util.Log

class SimpleKeyboardActionListener() : KeyboardView.OnKeyboardActionListener{
    override fun swipeRight() {
    }

    override fun onPress(primaryCode: Int) {
    }

    override fun onRelease(primaryCode: Int) {
    }

    override fun swipeLeft() {
    }

    override fun swipeUp() {
    }

    override fun swipeDown() {
    }

    override fun onKey(code: Int, keyCodes: IntArray?) {
        when (code) {
            in 48..57 -> futureKeyboardListener?.onNumberKeyDown(code.toChar().toString().toInt())
            10001 -> futureKeyboardListener?.onAddKeyDown()
            10002 -> futureKeyboardListener?.onSubKeyDown()
            10003 -> futureKeyboardListener?.onDelKeyDown()
            10004 -> futureKeyboardListener?.onDeleteKeyDown()
            10005 -> futureKeyboardListener?.onPriceQueueKeyDown()
            10006 -> futureKeyboardListener?.onPriceOpponentKeyDown()
            10007 -> futureKeyboardListener?.onPriceMarketKeyDown()
            10008 -> futureKeyboardListener?.onPriceLimitKeyDown()
            10009 -> futureKeyboardListener?.onClearKeyDown()
        }
    }

    override fun onText(text: CharSequence?) {
    }

    var futureKeyboardListener:FutureKeyboardListener? = null


}