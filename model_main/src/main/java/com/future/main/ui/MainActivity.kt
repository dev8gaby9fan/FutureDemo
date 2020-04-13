package com.future.main.ui

import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.launcher.ARouter
import com.fsh.common.base.BaseActivity
import com.fsh.common.base.BaseFragment
import com.fsh.common.base.CommonFragmentPagerAdapter
import com.fsh.common.model.ARouterPath
import com.fsh.common.util.ARouterUtils
import com.fsh.common.util.BottomNavigationViewHelper
import com.future.main.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    override fun layoutRes(): Int = R.layout.activity_main
    override fun getStatusBarColorRes(): Int = R.color.colorPrimaryDark
    private val pageList: ArrayList<BaseFragment> = ArrayList(4)
    private var isCreated: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPageList()
        initViews()
        isCreated = true
    }

    override fun onResume() {
        super.onResume()
        //如果没有登录交易账号，就直接显示行情界面
        if (isCreated && !ARouterUtils.getTradeService().isTradingLogin()) {
            fgt_container.currentItem = 0
        }
    }

    private fun initPageList() {
        val quoteFragment = ARouterUtils.getARouteComponent<BaseFragment>(ARouterPath.Page.PAGE_QUOTE_MAIN)
        val tradeFragment = ARouterUtils.getARouteComponent<BaseFragment>(ARouterPath.Page.PAGE_TRADE_MAIN)
        pageList.add(quoteFragment)
        pageList.add(tradeFragment)
    }

    private fun initViews() {
        nav_view.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_quote -> {
                    fgt_container.currentItem = 0
                }
                R.id.nav_trade -> {
                    //如果没有登录，就直接
                    if (!ARouterUtils.getTradeService().isTradingLogin()) {
//                        BottomNavigationViewHelper.selectItemByIndex(nav_view,0)
                        ARouter.getInstance()
                            .build(ARouterPath.Page.PAGE_TRADE_LOGIN)
                            .navigation(this@MainActivity)
                        nav_view.findViewById<View>(R.id.nav_quote).performClick()
//                        nav_view.selectedItemId = R.id.nav_quote
                    } else {
                        fgt_container.currentItem = 1
                    }
                }
            }
            true
        }
        fgt_container.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                nav_view.menu.getItem(position).isChecked = true
            }
        })
        val adapter = CommonFragmentPagerAdapter(supportFragmentManager, pageList)
        fgt_container.adapter = adapter
    }

}
