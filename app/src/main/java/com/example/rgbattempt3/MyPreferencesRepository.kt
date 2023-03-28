package com.example.rgbattempt3

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class MyPreferencesRepository private constructor(private val dataStore: DataStore<Preferences>){
    private val RSWITCH= booleanPreferencesKey("rswitch")
    private val RCOLOR= intPreferencesKey("rcolor")
    private val STRCLR= stringPreferencesKey("strclr")
    //    val strclr:Flow<String> = this.dataStore.data.map { prefs -> prefs[STRCLR]?: "000000000" }.distinctUntilChanged()
    val rswitch: Flow<Boolean> = this.dataStore.data.map { prefs -> (prefs[RSWITCH]?: false)}.distinctUntilChanged()
    val rcolor: Flow<Int> = this.dataStore.data.map { prefs -> (prefs[RCOLOR]?:0)}.distinctUntilChanged()
    private val GSWITCH= booleanPreferencesKey("gswitch")
    private val GCOLOR= intPreferencesKey("gcolor")
    val gswitch: Flow<Boolean> = this.dataStore.data.map { prefs -> (prefs[GSWITCH]?: false)}.distinctUntilChanged()
    val gcolor: Flow<Int> = this.dataStore.data.map { prefs -> (prefs[GCOLOR]?:0)}.distinctUntilChanged()
    private val BSWITCH= booleanPreferencesKey("bswitch")
    private val BCOLOR= intPreferencesKey("bcolor")
    val bswitch: Flow<Boolean> = this.dataStore.data.map { prefs -> (prefs[BSWITCH]?: false)}.distinctUntilChanged()
    val bcolor: Flow<Int> = this.dataStore.data.map { prefs -> (prefs[BCOLOR]?:0)}.distinctUntilChanged()

    private suspend fun saveInt(key:Preferences.Key<Int>,value:Int){
        this.dataStore.edit { prefs ->
            prefs[key]=value
        }
    }

    private suspend fun saveBoolean(key: Preferences.Key<Boolean>,value: Boolean){
        this.dataStore.edit { prefs-> prefs[key]=value }
    }
//    private suspend fun saveString(value:String){
//        this.dataStore.edit{prefs -> prefs[STRCLR]=value}
//    }




    suspend fun saveColor(value: Int,clr:String){
        val key: Preferences.Key<Int> = when(clr){
            "red" -> RCOLOR
            "green"->GCOLOR
            "blue"->BCOLOR
            else ->{
                throw NoSuchFieldException("Invalid Input")
            }
        }
        this.saveInt(key,value)
//        saveInt(RCOLOR,value)
    }
    //    suspend fun getAllColors():Array<Int>{
//        val array= arrayOf(0,0,0)
//        rcolor.collectLatest{
//            value->array[0]=value
//        }
//        gcolor.collectLatest{
//            value-> array[1]=value
//        }
//        bcolor.collectLatest{
//            value -> array[2]=value
//        }
//
//        return array
//    }
    suspend fun saveSwitchState(value:Boolean,clr:String){

        val key: Preferences.Key<Boolean> = when(clr){
            "red" -> RSWITCH
            "green"->GSWITCH
            "blue"->BSWITCH
            else ->{
                throw NoSuchFieldException("Invalid Input")
            }
        }
        this.saveBoolean(key,value)
//        saveBoolean(RSWITCH,value)
    }


    companion object{
        private const val PREFERENCES_DATA_FILE_NAME="settings"
        private var INSTANCE: MyPreferencesRepository? = null
        fun initialize(context: Context){
            val dataStore= PreferenceDataStoreFactory.create {
                context.preferencesDataStoreFile((PREFERENCES_DATA_FILE_NAME))
            }
            INSTANCE= MyPreferencesRepository(dataStore)
        }

        fun get():MyPreferencesRepository{
            return INSTANCE?: throw IllegalStateException("MyPreferenceRepository has not yet been Initialized()'ed")
        }
    }


}