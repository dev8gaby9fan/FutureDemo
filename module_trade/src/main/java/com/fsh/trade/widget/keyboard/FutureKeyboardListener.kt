package com.fsh.trade.widget.keyboard

interface FutureKeyboardListener{
    fun onNumberKeyDown(num:Int)
    fun onAddKeyDown()
    fun onSubKeyDown()
    fun onDelKeyDown()
    fun onDeleteKeyDown()
    fun onPriceQueueKeyDown()
    fun onPriceOpponentKeyDown()
    fun onPriceMarketKeyDown()
    fun onPriceLimitKeyDown()
    fun onClearKeyDown()
}

open class SimpleFutureKeyboardListener : FutureKeyboardListener {
    override fun onNumberKeyDown(num: Int) {

    }

    override fun onAddKeyDown() {

    }

    override fun onSubKeyDown() {

    }

    override fun onDelKeyDown() {

    }

    override fun onDeleteKeyDown() {

    }

    override fun onPriceQueueKeyDown() {

    }

    override fun onPriceOpponentKeyDown() {

    }

    override fun onPriceMarketKeyDown() {

    }

    override fun onPriceLimitKeyDown() {

    }
    override fun onClearKeyDown() {

    }
}