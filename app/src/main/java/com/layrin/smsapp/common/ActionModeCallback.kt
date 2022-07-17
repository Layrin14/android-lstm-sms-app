package com.layrin.smsapp.common

import android.view.Menu
import android.view.MenuItem
import android.view.ActionMode
import androidx.annotation.MenuRes

class ActionModeCallback(
    private val listener: SimpleCallback,
    @MenuRes private val menuRes: Int
) : ActionMode.Callback {

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        listener.onCreateActionMode()
        mode.menuInflater.inflate(menuRes, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        listener.onPrepareActionMode()
        return true
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem): Boolean {
        listener.onActionItemClicked(item)
        mode?.finish()
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        listener.onDestroyActionMode()
    }
}