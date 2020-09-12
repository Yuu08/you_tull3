package com.example.you_tulle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_set_customization.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Spinnerへ画面遷移をする関数
        fun IntentToTop2(view: View?){
            val intent = Intent(this, TopActivity::class.java)
            startActivity(intent)
        }
        btntotop.setOnClickListener{IntentToTop2(it)}
    }
}