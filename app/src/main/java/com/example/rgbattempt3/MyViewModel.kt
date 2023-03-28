package com.example.rgbattempt3

import android.graphics.Color
import android.util.Log
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
//const val LOG_TAG="Just Fucking Logging"

class MyViewModel:ViewModel() {

    private val prefs = MyPreferencesRepository.get()
    private val BoxColors = IntArray(3)
    private val _states= MutableStateFlow<IntArray>(IntArray(3))
    private val _bcolor = MutableStateFlow<Int>(0)
    private val _gcolor = MutableStateFlow<Int>(0)
    private val _rcolor = MutableStateFlow<Int>(0)
    private val _rswitch = MutableStateFlow<Boolean>(false)
    private val _bswitch = MutableStateFlow<Boolean>(false)
    private val _gswitch = MutableStateFlow<Boolean>(false)
    private val _colors= MutableStateFlow<IntArray>(IntArray(3))
    val bcolor = _bcolor.asStateFlow()
    val rcolor = _rcolor.asStateFlow()
    val gcolor = _gcolor.asStateFlow()
    val bswitch = _bswitch.asStateFlow()
    val rswitch=_rswitch.asStateFlow()
    val gswitch=_gswitch.asStateFlow()
    val colors=_colors.asStateFlow()
    val states=_states.asStateFlow()

    fun saveColor(i: Int, clr: String) {
        viewModelScope.launch {
            prefs.saveColor(i, clr)
        }
    }

    fun saveSwitch(state: Boolean, clr: String) {
        viewModelScope.launch {
            prefs.saveSwitchState(state, clr)
        }
    }


//    fun Reset() {
//        viewModelScope.launch {
//            prefs.saveColor(0, "red")
//            prefs.saveColor(0, "green")
//            prefs.saveColor(0, "blue")
//            prefs.saveSwitchState(false, "red")
//            prefs.saveSwitchState(false, "green")
//            prefs.saveSwitchState(false, "blue")
//        }
//    }

    fun loadUIValues() {
        viewModelScope.launch {
            combine(prefs.rcolor,prefs.gcolor,prefs.bcolor){
                r,g,b->
                Triple(r,g,b)
            }.collectLatest {
                var arr=IntArray(3)
                arr[0]=it.first
                arr[1]=it.second
                arr[2]=it.third
                _rcolor.value = it.first
                _gcolor.value = it.second
                _bcolor.value = it.third
                _colors.value = arr
            }
        }

        viewModelScope.launch {
            combine(prefs.rswitch,prefs.gswitch,prefs.bswitch){
                    r,g,b->
                Triple(r,g,b)
            }.collectLatest {
                _rswitch.value = it.first
                _gswitch.value = it.second
                _bswitch.value = it.third
            }
        }

    }
}






