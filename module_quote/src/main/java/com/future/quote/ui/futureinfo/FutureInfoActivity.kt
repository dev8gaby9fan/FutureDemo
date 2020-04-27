package com.future.quote.ui.futureinfo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Route
import com.fsh.common.base.BaseActivity
import com.fsh.common.base.BaseFragment
import com.fsh.common.base.CommonFragmentPagerAdapter
import com.fsh.common.model.ARouterPath
import com.fsh.common.util.Omits
import com.future.quote.R
import com.future.quote.ui.futureinfo.charts.FutureChartsFragment
import com.future.quote.ui.futureinfo.information.FutureInformationFragment
import kotlinx.android.synthetic.main.quote_activity_future_info.*

@Route(path = ARouterPath.Page.PAGE_QUOTE_KLINE)
class FutureInfoActivity : BaseActivity(){
    private val fragments:MutableList<BaseFragment> = ArrayList(2)
    private lateinit var pagerAdapter:CommonFragmentPagerAdapter

    companion object{
        private const val INSTRUMENT_ID = "INSTRUMENT"
        fun startActivity(context:Context,instrumentId:String){
            context.startActivity(Intent(context,FutureInfoActivity::class.java).apply {
                putExtra(INSTRUMENT_ID,instrumentId)
            })
        }
    }

    override fun layoutRes(): Int = R.layout.quote_activity_future_info
    override fun getStatusBarColorRes(): Int = R.color.colorPrimaryDark

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val instrumentId = intent.getStringExtra(INSTRUMENT_ID)
        if(Omits.isOmit(instrumentId)){
            finish()
            return
        }
        initViews(instrumentId!!)
    }

    private fun initViews(instrumentId:String){
        val tabTitles = resources.getStringArray(R.array.quote_future_tabs)
        fragments.add(FutureChartsFragment.newInstance(instrumentId))
        fragments.add(FutureInformationFragment.newInstance(instrumentId))
        pagerAdapter = CommonFragmentPagerAdapter(supportFragmentManager,fragments)
        view_pager.adapter = pagerAdapter
        tab_future.setupWithViewPager(view_pager)
//        tab_future.addTab(tab_future.newTab().setText(tabTitles[0]))
//        tab_future.addTab(tab_future.newTab().setText(tabTitles[1]))
        for(tabIndex in  0 until tab_future.tabCount){
            tab_future.getTabAt(tabIndex)?.text = tabTitles[tabIndex]
        }
        Log.d("FutureInfoActivity","tab_future tab count ${tab_future.tabCount}")
    }
}