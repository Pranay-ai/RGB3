package com.example.rgbattempt3

import android.content.ContentValues.TAG
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(),SeekBar.OnSeekBarChangeListener{
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
    private lateinit var myViewModel: MyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        connectViews()
        MyPreferencesRepository.initialize(this)
        myViewModel = ViewModelProvider(this)[MyViewModel::class.java]

        lifecycleScope.launch{
            collectFlows()
        }
        myViewModel.loadUIValues()
        setupListeners()

    }

    private suspend fun collectFlows() {
        lifecycleScope.async {
            myViewModel.gcolor.collectLatest {
                gseekbar.progress = it
                geditview.setText(String.format("%.2f", (it.toDouble() / 255.toDouble())))
            }
        }
        lifecycleScope.async {
            myViewModel.rcolor.collectLatest {
                rseekbar.progress = it
                reditview.setText(String.format("%.2f", (it.toDouble() / 255.toDouble())))
            }
        }
        lifecycleScope.async {
            myViewModel.bcolor.collectLatest {
                bseekbar.progress = it
                beditview.setText(String.format("%.2f", (it.toDouble() / 255.toDouble())))
            }
        }
        lifecycleScope.async {
            myViewModel.colors.collectLatest {
                displayColor.setBackgroundColor(Color.argb(255,it[0],it[1],it[2]))
            }
        }
        lifecycleScope.async {
            myViewModel.rswitch.collectLatest {
                if (it) {
                    rseekbar.isEnabled = true
                    reditview.isEnabled = true
                } else {
                    reditview.isEnabled = false
                    rseekbar.isEnabled = false
                }
            }

        }

        lifecycleScope.async {
            myViewModel.gswitch.collectLatest {
                if (it) {
                    gseekbar.isEnabled = true
                    geditview.isEnabled = true
                } else {
                    geditview.isEnabled = false
                    gseekbar.isEnabled = false
                }
            }

        }

        lifecycleScope.async {
            myViewModel.bswitch.collectLatest {
                if (it) {
                    bseekbar.isEnabled = true
                    beditview.isEnabled = true
                } else {
                    beditview.isEnabled = false
                    bseekbar.isEnabled = false
                }
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
        gseekbar.setOnSeekBarChangeListener(this)
        geditview.addTextChangedListener(ETlistener("green"))
        rseekbar.setOnSeekBarChangeListener(this)
        reditview.addTextChangedListener(ETlistener("red"))
        bseekbar.setOnSeekBarChangeListener(this)
        beditview.addTextChangedListener(ETlistener("blue"))
        SWlistener(rswitch,"red")
        SWlistener(gswitch, "green")
        SWlistener(bswitch,"blue")
        reset.setOnClickListener {
            myViewModel.saveColor(0,"red")
            myViewModel.saveColor(0,"green")
            myViewModel.saveColor(0,"blue")
            myViewModel.saveSwitch(false,"red")
            myViewModel.saveSwitch(false,"green")
            myViewModel.saveSwitch(false,"blue")
        }


    }

    //SWITCH LISTENER FUNCTION

    fun SWlistener(SW:Switch,clr:String){
        SW.setOnCheckedChangeListener {  buttonView, isChecked ->
            myViewModel.saveSwitch(isChecked,clr)
            when(clr){
                "red"-> States[0]=isChecked
                "green"-> States[1]=isChecked
                "blue"-> States[2]=isChecked
            }
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
                            val message = "Max Value is 1!"
                            val duration = Toast.LENGTH_SHORT // or Toast.LENGTH_LONG
                            val toast = Toast.makeText(applicationContext, message, duration)
                            toast.show()
                            myViewModel.saveColor(255,clr)
                        }
                        else{
                            myViewModel.saveColor((ColorValue*255).toInt(),clr)
                        }
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



            }
        }
        return mySeekbarListener
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        seekBar?.let {
            when(it.id){
                R.id.sb_red->{
                    myViewModel.saveColor(it.progress, "red")
                }
                R.id.sb_green->{
                    myViewModel.saveColor(it.progress, "green")
                }
                R.id.sb_blue->{
                    myViewModel.saveColor(it.progress, "blue")
                }
            }
        }
    }


}



