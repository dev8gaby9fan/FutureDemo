package com.future.quote.ui.futureinfo.information

import android.os.Bundle
import com.fsh.common.base.BaseLazyFragment
import com.future.quote.R

class FutureInformationFragment : BaseLazyFragment(){

    companion object{
        private const val INSTRUMENT_ID = "INSTRUMENT_ID"
        fun newInstance(instrumentId:String):FutureInformationFragment{
            return FutureInformationFragment().apply {
                arguments = Bundle().also { it.putString(INSTRUMENT_ID,instrumentId) }
            }
        }
    }

    override fun layoutRes(): Int = R.layout.quote_fragment_future_infomation

    override fun lazyLoading(){

    }



}