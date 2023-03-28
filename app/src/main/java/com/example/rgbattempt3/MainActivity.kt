package com.example.rgbattempt3

import android.content.ContentValues.TAG
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Display
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    var States= arrayOf(false,false,false)

    lateinit var rswitch: Switch
    lateinit var rseekbar: SeekBar
    lateinit var reditview: EditText
    lateinit var gswitch: Switch
    lateinit var gseekbar: SeekBar
    lateinit var geditview: EditText
    lateinit var bswitch: Switch
    lateinit var bseekbar: SeekBar
    lateinit var beditview: EditText
    lateinit var displayColor: TextView
    lateinit var reset: Button
    var BoxColor= arrayOf(0,0,0)
    private lateinit var MyViewModel: MyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        connectViews()
        setupListeners()
        MyPreferencesRepository.initialize(this)
        MyViewModel = ViewModelProvider(this)[MyViewModel::class.java]
        MyViewModel.loadUIValues()
        GlobalScope.launch {

            collectFlows()
        }
    }

    suspend fun collectFlows(){
        MyViewModel.rcolor.collectLatest {
            rseekbar.setProgress(it)
            reditview.setText(String.format("%.2f", (it.toDouble() / 255.toDouble())))
        }
        MyViewModel.gcolor.collectLatest {
            rseekbar.setProgress(it)
            reditview.setText(String.format("%.2f", (it.toDouble() / 255.toDouble())))
        }
        MyViewModel.bcolor.collectLatest {
            rseekbar.setProgress(it)
            reditview.setText(String.format("%.2f", (it.toDouble() / 255.toDouble())))
        }
        MyViewModel.colors.collectLatest {
            displayColor.setBackgroundColor(Color.argb(255,it[0],it[1],it[2]))
        }
        MyViewModel.rswitch.collectLatest {
            if (it) {
                rseekbar.isEnabled = true
                reditview.isEnabled = true
            } else {
                reditview.isEnabled = false
                rseekbar.isEnabled = false
            }
        }

        MyViewModel.gswitch.collectLatest {
            if (it) {
                gseekbar.isEnabled = true
                geditview.isEnabled = true
            } else {
                geditview.isEnabled = false
                gseekbar.isEnabled = false
            }
        }

        MyViewModel.bswitch.collectLatest {
            if (it) {
                bseekbar.isEnabled = true
                beditview.isEnabled = true
            } else {
                beditview.isEnabled = false
                bseekbar.isEnabled = false
            }
        }

    }
    // SET UP CONNECT VIEWS
    private fun connectViews() {
        rswitch = findViewById(R.id.sw_red)
        rseekbar = findViewById(R.id.sb_red)
        reditview = findViewById(R.id.tv_red)
        gswitch = findViewById(R.id.sw_green)
        gseekbar = findViewById(R.id.sb_green)
        geditview = findViewById(R.id.tv_green)
        bswitch = findViewById(R.id.sw_blue)
        bseekbar = findViewById(R.id.sb_blue)
        beditview = findViewById(R.id.tv_blue)
        displayColor = findViewById(R.id.tvColor)
        reset = findViewById(R.id.reset)
    }
    //SET UP LISTENERS
    private fun setupListeners() {
        gseekbar.setOnSeekBarChangeListener(SBlistener("green"))
        geditview.addTextChangedListener(ETlistener("green"))
        rseekbar.setOnSeekBarChangeListener(SBlistener("red"))
        reditview.addTextChangedListener(ETlistener("red"))
        bseekbar.setOnSeekBarChangeListener(SBlistener("blue"))
        beditview.addTextChangedListener(ETlistener("blue"))
        SWlistener(rswitch,"red")
        SWlistener(gswitch, "green")
        SWlistener(bswitch,"blue")
        reset.setOnClickListener {
            MyViewModel.saveColor(0,"red")
            MyViewModel.saveColor(0,"green")
            MyViewModel.saveColor(0,"blue")
            MyViewModel.saveSwitch(false,"red")
            MyViewModel.saveSwitch(false,"green")
            MyViewModel.saveSwitch(false,"blue")
        }


    }

    //SWITCH LISTENER FUNCTION

    fun SWlistener(SW:Switch,clr:String){
        SW.setOnCheckedChangeListener {  buttonView, isChecked ->
            MyViewModel.saveSwitch(isChecked,clr)
            when(clr){
                "red"-> States[0]=isChecked
                "green"-> States[1]=isChecked
                "blue"-> States[2]=isChecked
            }
            updateColor()
        }
    }
    //EDIT TEXT LISTENER FUNCTION
    fun ETlistener(clr:String):TextWatcher {
        var editTextListener = object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.toString().length == 4) {
                    try{
                        var ColorValue=s.toString().toFloat()
                        if (ColorValue>1){
                            ColorValue=1F
                            val message = "Max Value is 1!"
                            val duration = Toast.LENGTH_SHORT // or Toast.LENGTH_LONG
                            val toast = Toast.makeText(applicationContext, message, duration)
                            toast.show()
                            MyViewModel.saveColor(255,clr)
                        }
                        else{
                            MyViewModel.saveColor((ColorValue*255).toInt(),clr)
                        }
                        updateColor()
                    }
                    catch (e:Exception){

                    }
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }
        }
        return editTextListener
    }


//     SETUP SEEKBAR LISTENER
    fun updateColor(){

        var Mem=IntArray(3)
        for (i in 0..2){
            if(States[i]) Mem[i]=BoxColor[i]
            else Mem[i]=0
        }
        displayColor.setBackgroundColor(Color.argb(255,Mem[0],Mem[1],Mem[2]))
        Log.d(TAG, "BoxColor:${BoxColor.joinToString(",")} ")
        Log.d(TAG, "MEMColor:${Mem.joinToString(",")} ")
        Log.d(TAG, "StateColor:${States.joinToString(",")} ")
    }
    fun SBlistener(clr:String):SeekBar.OnSeekBarChangeListener{
        var mySeekbarListener:SeekBar.OnSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                MyViewModel.saveColor(seekBar.progress,clr)

            }
        }
        return mySeekbarListener
    }


}



