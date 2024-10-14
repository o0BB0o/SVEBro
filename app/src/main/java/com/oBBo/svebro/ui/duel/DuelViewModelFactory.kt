package com.oBBo.svebro.ui.duel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DuelViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DuelViewModel::class.java)) {
            return DuelViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
