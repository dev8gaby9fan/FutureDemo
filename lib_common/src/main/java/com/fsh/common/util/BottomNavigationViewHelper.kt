package com.fsh.common.util

import androidx.annotation.IdRes
import com.google.android.material.bottomnavigation.BottomNavigationView

object BottomNavigationViewHelper {

    fun selectItemByIndex(navView:BottomNavigationView,index:Int){
        for(indexInNav in 0 until navView.menu.size()){
            navView.menu.getItem(indexInNav).isChecked = index == indexInNav
        }
    }

    fun selectItemById(navView:BottomNavigationView,@IdRes itemId:Int){
        for(indexInNav in 0 until navView.menu.size()){
            val item = navView.menu.getItem(indexInNav)
            item.isChecked = item.itemId == itemId
        }
    }
}