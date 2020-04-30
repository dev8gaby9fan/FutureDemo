package com.future.quote.ui.futureinfo.charts

import android.os.Bundle
import android.widget.TextView
import com.fsh.common.base.BaseLazyFragment
import com.fsh.common.base.CommonFragmentPagerAdapter
import com.fsh.common.ext.viewModelOf
import com.fsh.common.model.QuoteEntity
import com.fsh.common.util.NumberUtils
import com.fsh.common.util.Omits
import com.future.quote.R
import com.future.quote.enums.ChartType
import com.future.quote.enums.FutureChartType
import com.future.quote.service.QuoteInfoMgr
import com.future.quote.viewmodel.FutureQuoteViewModel
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.quote_fragment_future_charts.*

/**
 * 行情界面(分时\K线\最新行情)
 */
class FutureChartsFragment : BaseLazyFragment(){
    private val fragments:MutableList<BaseChartsFragment> = ArrayList()
    private lateinit var containerAdapter:CommonFragmentPagerAdapter
    private var viewMode:FutureQuoteViewModel? = null
    private val disposable:CompositeDisposable = CompositeDisposable()
    companion object{
        private const val INSTRUMENT_ID = "INSTRUMENT_ID"
        fun newInstance(instrumentId:String):FutureChartsFragment{
            return FutureChartsFragment().apply {
                arguments = Bundle().apply {
                    putString(INSTRUMENT_ID,instrumentId)
                }
            }
        }
    }

    override fun layoutRes(): Int = R.layout.quote_fragment_future_charts

    override fun lazyLoading() {
        val instrumentId = arguments?.getString(INSTRUMENT_ID)!!
        initViews(instrumentId)
        loadDatas(instrumentId)
    }

    private fun initViews(instrumentId: String){
        val futureChartTypeList = FutureChartType.TYPE_LIST
        for(type in futureChartTypeList){
            if(type.chartType == ChartType.Line){
                fragments.add(CurrentDayLineChartFragment.newInstance(instrumentId))
            }else{
                fragments.add(KLineChartFragment.newInstance(instrumentId,type))
            }
        }
        containerAdapter = CommonFragmentPagerAdapter(childFragmentManager,fragments)
        charts_container.adapter = containerAdapter
        tab_charts.setupWithViewPager(charts_container)
        for(tabIndex in 0 until tab_charts.tabCount){
            tab_charts.getTabAt(tabIndex)?.text = futureChartTypeList[tabIndex].title
        }
        charts_container.offscreenPageLimit = tab_charts.tabCount
    }

    private fun loadDatas(instrumentId: String){
        viewMode = viewModelOf<FutureQuoteViewModel>().value.apply {
            disposable.add(quoteData.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { onQuoteUpdate(it) })
        }
        viewMode?.subscribeQuote(instrumentId)
    }

    private fun onQuoteUpdate(quoteEntity:QuoteEntity){
        val instrument = QuoteInfoMgr.mgr.getInstrument(quoteEntity.instrument_id)
        updateQuoteText(tv_last_price,quoteEntity.last_price,quoteEntity.quoteTextColor,instrument?.priceTick)
        updateQuoteText(tv_ins_updown,quoteEntity.updown,quoteEntity.quoteTextColor,instrument?.priceTick)
        updateQuoteText(tv_ins_updown_ratio,quoteEntity.updown_ratio,quoteEntity.quoteTextColor,instrument?.priceTick,false)
        updateQuoteText(tv_bid_price,quoteEntity.bid_price1,quoteEntity.quoteTextColor,instrument?.priceTick)
        updateQuoteText(tv_ask_price,quoteEntity.bid_price1,quoteEntity.quoteTextColor,instrument?.priceTick)
        updateQuoteText(tv_high,quoteEntity.highest,quoteEntity.quoteTextColor,instrument?.priceTick)
        updateQuoteText(tv_low,quoteEntity.lowest,quoteEntity.quoteTextColor,instrument?.priceTick)

        updateQuoteTextWithoutColor(tv_vol,quoteEntity.volume,"1")
        updateQuoteTextWithoutColor(tv_open,quoteEntity.open,instrument?.priceTick)
        updateQuoteTextWithoutColor(tv_pre_close,quoteEntity.pre_close,instrument?.priceTick)
        updateQuoteTextWithoutColor(tv_pre_settlement,quoteEntity.pre_settlement,instrument?.priceTick)
        updateQuoteTextWithoutColor(tv_price_avg,quoteEntity.average,instrument?.priceTick)
        updateQuoteTextWithoutColor(tv_pos_vol,quoteEntity.pre_open_interest,"1")
        updateQuoteTextWithoutColor(tv_oi,quoteEntity.open_interest,"1")
        updateQuoteTextWithoutColor(tv_ask_vol,quoteEntity.ask_volume1,"1")
        updateQuoteTextWithoutColor(tv_bid_vol,quoteEntity.bid_volume1,"1")
    }

    private fun updateQuoteText(textView:TextView,property:String,textColor:Int,priceTick:String?,format:Boolean = true){
        if(textView.text != property){
            textView.text = if(format) NumberUtils.formatNum(property,priceTick) else property
            textView.setTextColor(resources.getColor(textColor))
        }
    }

    private fun updateQuoteTextWithoutColor(textView: TextView,property: String,priceTick: String?){
        if(Omits.isOmit(property)){
            textView.text = Omits.OmitPrice
        }else{
            textView.text = NumberUtils.formatNum(property,priceTick)
        }
    }

    override fun onDestroyView() {
        disposable.clear()
        fragments[charts_container.currentItem].unSetChart()
        super.onDestroyView()
    }


}