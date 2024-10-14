package com.oBBo.svebro.ui.diy

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class DIYViewModel : ViewModel() {
    val zipRealUri = MutableLiveData<Uri>()
    val zipFileName = MutableLiveData<String>()

    val selectedClass = MutableLiveData<Int>().apply { value = 0 }

    val isStatusTextViewVisible = MutableLiveData<Boolean>().apply { value = false }
    val statusText = MutableLiveData<String>()

    val leaderNameInput = MutableLiveData<String>()

    val isLoadSuccess = MutableLiveData<Boolean>().apply { value = false }
    val isProcessingZip = MutableLiveData<Boolean>().apply { value = false }

    @BindingAdapter("selectedItemPosition")
    fun setSelectedItemPosition(spinner: Spinner, selectedPosition: Int?) {
        selectedPosition?.let {
            if (spinner.selectedItemPosition != it) {
                spinner.setSelection(it)
            }
        }
    }

    // Inverse binding adapter to get the selected item position from Spinner to ViewModel
    @InverseBindingAdapter(attribute = "selectedItemPosition")
    fun getSelectedItemPosition(spinner: Spinner): Int {
        return spinner.selectedItemPosition
    }

    // Listener to trigger two-way binding updates
    @BindingAdapter("selectedItemPositionAttrChanged")
    fun setItemSelectedListener(spinner: Spinner, listener: InverseBindingListener?) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                listener?.onChange()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle no selection case, if needed
            }
        }
    }

}