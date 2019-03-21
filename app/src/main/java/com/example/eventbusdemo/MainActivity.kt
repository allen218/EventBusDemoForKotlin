package com.example.eventbusdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.eventbusdemo.eventbus.EventBus
import com.example.eventbusdemo.eventbus.Subscribe

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBus.register(this)
        EventBus.post("1", "allen")
    }


    @Subscribe(arrayOf("1", "2"))
    fun test(str: String?, num: Int?) {
        Log.i("TAG", "str: $str, num: $num")
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.unRegister(this)
    }
}
