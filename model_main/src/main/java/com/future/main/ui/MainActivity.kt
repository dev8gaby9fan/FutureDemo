package com.future.main.ui

import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.fsh.common.base.BaseActivity
import com.fsh.common.base.BaseFragment
import com.fsh.common.base.CommonFragmentPagerAdapter
import com.fsh.common.model.ARouterPath
import com.fsh.common.util.ARouterUtils
import com.future.main.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    override fun layoutRes(): Int = R.layout.activity_main

    override fun getStatusBarColorRes(): Int = R.color.colorPrimaryDark

    private val pageList:ArrayList<BaseFragment> = ArrayList(4)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPageList()
        initViews()
    }

    private fun initPageList(){
        val quoteFragment = ARouterUtils.getARouteComponent<BaseFragment>(ARouterPath.Page.PAGE_QUOTE_MAIN)
        val tradeFragment = ARouterUtils.getARouteComponent<BaseFragment>(ARouterPath.Page.PAGE_TRADE_MAIN)
        if(quoteFragment != null && tradeFragment != null){
            pageList.add(quoteFragment)
            pageList.add(tradeFragment)
        }
    }

    private fun initViews(){
        nav_view.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_quote ->{
                    fgt_container.currentItem = 0
                }
                R.id.nav_trade ->{
                    fgt_container.currentItem = 1
                }
            }
            true
        }
        fgt_container.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}

            override fun onPageSelected(position: Int) {
                nav_view.menu.getItem(position).isChecked = true
            }
        })
        val adapter = CommonFragmentPagerAdapter(supportFragmentManager,pageList)
        fgt_container.adapter = adapter
    }

}
