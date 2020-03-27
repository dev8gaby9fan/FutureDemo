package com.fsh.common.base

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

@SuppressLint("WrongConstant")
class CommonFragmentPagerAdapter(fg:FragmentManager,private val fragments:List<BaseFragment>,private var titles:List<String>? = null) : FragmentPagerAdapter(fg,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size

    override fun getPageTitle(position: Int):CharSequence? = titles?.get(position)
}