package com.future.quote.ui.main

import android.util.Log
import android.view.Gravity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.fsh.common.base.BaseLazyFragment
import com.fsh.common.ext.addChildFragment
import com.fsh.common.ext.findFragmentById
import com.fsh.common.ext.viewModelOf
import com.fsh.common.model.ARouterPath
import com.fsh.common.util.ARouterUtils
import com.future.quote.R
import com.future.quote.event.BaseEvent
import com.future.quote.service.QuoteInfoMgr
import com.future.quote.viewmodel.QuoteMainViewModel
import kotlinx.android.synthetic.main.fragment_quote.*
import kotlinx.android.synthetic.main.fragment_quote_main.*

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/10
 * description: 行情主界面
 */

@Route(path = ARouterPath.Page.PAGE_QUOTE_MAIN)
class QuoteMainFragment : BaseLazyFragment(),MenuEventListener{


    override fun layoutRes(): Int = R.layout.fragment_quote
    private lateinit var quoteMainViewModel:QuoteMainViewModel
    private lateinit var menuFragment: IDrawerMenuFragment
    private lateinit var contentFragment: IContentFragment

    override fun lazyLoading() {
        val tradeService = ARouterUtils.getTradeService()
        Log.d("QuoteMainFragment","lazyLoading $tradeService")
        initViews()
        initDatas()
    }

    private fun initViews(){
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        val toggle = ActionBarDrawerToggle(activity,drawer,toolbar,R.string.drawer_open,R.string.drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        var drawerMenuFragment = findFragmentById(R.id.fl_quote_menu)
        if(drawerMenuFragment == null){
            drawerMenuFragment = QuoteMenuFragment()
            drawerMenuFragment.menuEventListener = this
            addChildFragment(R.id.fl_quote_menu,drawerMenuFragment!!)
        }
        menuFragment = drawerMenuFragment as IDrawerMenuFragment
        var mainPageFragment = findFragmentById(R.id.fragment_container)
        if(mainPageFragment == null){
            mainPageFragment = QuoteListFragment()
            addChildFragment(R.id.fragment_container,mainPageFragment)
        }
        contentFragment = mainPageFragment as IContentFragment
    }

    private fun initDatas(){
        setLoadingCancelable(false)
        setLoadingDialogMessage("正在加载合约数据")
        showLoadingDialog()
        quoteMainViewModel = viewModelOf<QuoteMainViewModel>().value
        quoteMainViewModel.insEvent.observe(this, Observer<BaseEvent> {
            handleLoadInsResp(it)
            dismissLoading()
        })
    }

    private fun handleLoadInsResp(event:BaseEvent){
        Log.d("QuoteMainFragment","Thread ${Thread.currentThread().name} load ins resp ${event.action}")
        if(event.action == BaseEvent.ACTION_LOAD_INS_OK){
            menuFragment.onLoadInsOk()
        }
    }

    override fun switchExchange(exchangeId: String) {
        drawer.closeDrawer(Gravity.LEFT)
        toolbar.title = QuoteInfoMgr.mgr.getExchange(exchangeId).name
        contentFragment.onSwitchExchange(exchangeId)
    }
}

/**
 * 和菜单项互动的接口
 */
interface IDrawerMenuFragment{
    fun onLoadInsOk()
}

/**
 * 和内容界面互动的接口
 */
interface IContentFragment{
    fun onSwitchExchange(id:String)
}