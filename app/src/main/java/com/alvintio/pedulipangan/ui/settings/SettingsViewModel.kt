package com.alvintio.pedulipangan.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvintio.pedulipangan.data.repo.UserPreferences
import kotlinx.coroutines.launch

class SettingsViewModel(private val pref: UserPreferences) : ViewModel() {
    private val _logoutComplete = MutableLiveData<Boolean>()
    val logoutComplete: LiveData<Boolean> = _logoutComplete

    private val _text = MutableLiveData<String>().apply {
        value = "This is settings Fragment"
    }

    val text: LiveData<String> = _text

    fun userLogout() {
        viewModelScope.launch {
            pref.logout()
            _logoutComplete.value = true
        }
    }
}