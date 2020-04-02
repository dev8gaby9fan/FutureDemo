package com.fsh.trade.widget.keyboard

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.inputmethodservice.Keyboard
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.fsh.trade.R
import kotlinx.android.synthetic.main.layout_future_keyboard.view.*

class FutureKeyboard : FrameLayout {
    var isShowing: Boolean = false
    private val futureKeyboardView: FutureKeyboardView
    private val priceKeyboard: Keyboard
    private val volumeKeyboard: Keyboard
    private val keyboardListener: SimpleKeyboardActionListener = SimpleKeyboardActionListener()
    private var keyboardType: KeyboardType = KeyboardType.Price
    private var showAnim: ObjectAnimator? = null
    private var dismissAnim: ObjectAnimator? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        val layout = LayoutInflater.from(context).inflate(R.layout.layout_future_keyboard, null)
        layout.iv_close.setOnClickListener { dismiss() }
        futureKeyboardView = layout.future_keyboard
        priceKeyboard = Keyboard(context, R.xml.keyboard_price)
        volumeKeyboard = Keyboard(context, R.xml.keyboard_volume)
        initKeyBoard()
        isShowing = visibility == View.VISIBLE
        addView(layout)
    }

    private fun initKeyBoard() {
        futureKeyboardView.isEnabled = true
        futureKeyboardView.isPreviewEnabled = false
        futureKeyboardView.setOnKeyboardActionListener(keyboardListener)
        setKeyboardType(keyboardType)

    }

    fun setFutureKeyboardListener(listener: FutureKeyboardListener?) {
        keyboardListener.futureKeyboardListener = listener
    }

    fun show(type:KeyboardType) {
        setKeyboardType(type)
        if (isShowing && View.VISIBLE == visibility) {
            return
        }
        showAnim?.cancel()
        val params = floatArrayOf(0f, 1f)
        showAnim = ObjectAnimator.ofFloat(this, "translationY", *params).apply {
            duration = 2000
        }
        visibility = View.VISIBLE
        showAnim?.start()
        isShowing = true
        Log.d("FutureKeyboard", "show called --> $isShowing $visibility")
    }

    fun dismiss() {
        if (!isShowing && View.INVISIBLE == visibility) {
            return
        }
//        dismissAnim?.cancel()
        if (dismissAnim == null) {
//            val params = floatArrayOf(0f,height.toFloat())
//            dismissAnim = ObjectAnimator.ofFloat(this,"translationY",*params).apply {
//                duration = 5000
//            }
        }
        visibility = View.INVISIBLE
//        dismissAnim?.start()
        isShowing = false
        Log.d("FutureKeyboard", "dismiss called --> $isShowing $visibility")
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (showAnim != null) {
            showAnim?.cancel()
            showAnim = null
        }
        if (dismissAnim != null) {
            dismissAnim?.cancel()
            dismissAnim = null
        }
    }

    private fun setKeyboardType(type: KeyboardType) {
        keyboardType = type
        futureKeyboardView.keyboard =
            if (keyboardType == KeyboardType.Price) priceKeyboard else volumeKeyboard
    }

    enum class KeyboardType {
        Price, Volume
    }
}