package com.fsh.trade.widget

import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.fsh.common.base.BaseBottomSheetDialog
import com.fsh.common.model.InstrumentInfo
import com.fsh.trade.R
import kotlinx.android.synthetic.main.layout_keyboard.*

class FutureKeyboardView(private val type:Int) : BaseBottomSheetDialog(){
    override fun getLayoutRes(): Int = R.layout.layout_keyboard
    private var keyboard:Keyboard? = null
    var keyDownListener:OnKeyDownListener? = null
    private var instrument:InstrumentInfo? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val keyBoardView = if(type == KEYBOARD_TYPE_PRICE) R.xml.keyboard_price else R.xml.keyboard_volume
        keyboard = Keyboard(context,keyBoardView)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view_keyboard.keyboard = keyboard
        view_keyboard.isEnabled = true
        view_keyboard.isPreviewEnabled = false
        view_keyboard.setOnKeyboardActionListener(object : KeyboardView.OnKeyboardActionListener{
            override fun swipeRight() {}
            override fun onPress(primaryCode: Int) {}
            override fun onRelease(primaryCode: Int) {}
            override fun swipeLeft() {}
            override fun swipeUp() {}
            override fun swipeDown() {}
            override fun onText(text: CharSequence?) {}
            override fun onKey(primaryCode: Int, keyCodes: IntArray?) = handleKeyboardAction(primaryCode)
        })
    }

    private fun handleKeyboardAction(code:Int){
        when(code){
            in 48..57 -> keyDownListener?.onNumberKeyDown(code.toChar().toString().toInt())
            10001 -> keyDownListener?.onAddKeyDown()
            10002 -> keyDownListener?.onSubKeyDown()
            10003 -> keyDownListener?.onDelKeyDown()
            10004 -> keyDownListener?.onDeleteKeyDown()
            10005 -> keyDownListener?.onPriceQueueKeyDown()
            10006 -> keyDownListener?.onPriceOpponentKeyDown()
            10007 -> keyDownListener?.onPriceMarketKeyDown()
            10008 -> keyDownListener?.onPriceLimitKeyDown()
            10009 -> keyDownListener?.onClearKeyDown()
        }
    }

    fun show(ins:InstrumentInfo?,fmg:FragmentManager){
        if(ins == null){
            return
        }
        if(isShowing){
            return
        }
        instrument = ins
        val tag = if(type == KEYBOARD_TYPE_PRICE) TAG_PRICE else TAG_VOLUME
        show(fmg,tag)
    }

    companion object{
        const val KEYBOARD_TYPE_PRICE = 1001
        const val KEYBOARD_TYPE_VOLUME = 1002
        private const val TAG_PRICE = "PRICE"
        private const val TAG_VOLUME = "PRICE"
    }

    interface OnKeyDownListener{
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

   open class SimpleOnKeyDownListener() : OnKeyDownListener{
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
}