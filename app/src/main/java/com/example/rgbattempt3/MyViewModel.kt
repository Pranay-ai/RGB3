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
    private val _states= MutableSharedFlow<IntArray>()
    private val _bcolor = MutableSharedFlow<Int>()
    private val _gcolor = MutableSharedFlow<Int>()
    private val _rcolor = MutableSharedFlow<Int>()
    private val _rswitch = MutableSharedFlow<Boolean>()
    private val _bswitch = MutableSharedFlow<Boolean>()
    private val _gswitch = MutableSharedFlow<Boolean>()
    private val _colors= MutableSharedFlow<IntArray>()
    val bcolor = _bcolor.asSharedFlow()
    val rcolor = _rcolor.asSharedFlow()
    val gcolor = _rcolor.asSharedFlow()
    val bswitch = _bswitch.asSharedFlow()
    val rswitch=_rswitch.asSharedFlow()
    val gswitch=_gswitch.asSharedFlow()
    val colors=_colors.asSharedFlow()
    val states=_states.asSharedFlow()

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
                _rcolor.emit(it.first)
                _gcolor.emit(it.second)
                _bcolor.emit(it.third)
                _colors.emit(arr)
            }
        }

        viewModelScope.launch {
            combine(prefs.rswitch,prefs.gswitch,prefs.bswitch){
                    r,g,b->
                Triple(r,g,b)
            }.collectLatest {
                _rswitch.emit(it.first)
                _gswitch.emit(it.second)
                _bswitch.emit(it.third)
            }
        }

    }
}






