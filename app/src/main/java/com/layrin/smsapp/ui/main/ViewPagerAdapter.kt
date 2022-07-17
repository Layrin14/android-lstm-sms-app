package com.layrin.smsapp.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    private val fragmentList: Array<Int>,
    fm: FragmentManager,
    lf: Lifecycle
) : FragmentStateAdapter(fm, lf) {
    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment =
        MainFragment.newInstance(fragmentList[position])
}