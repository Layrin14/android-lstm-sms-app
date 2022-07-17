package com.layrin.smsapp.common

import android.view.MenuItem

interface SimpleCallback {
    fun onCreateActionMode()
    fun onPrepareActionMode()
    fun onActionItemClicked(item: MenuItem)
    fun onDestroyActionMode()
}